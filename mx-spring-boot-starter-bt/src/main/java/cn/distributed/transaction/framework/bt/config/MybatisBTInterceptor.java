package cn.distributed.transaction.framework.bt.config;

import cn.distributed.transaction.common.exception.BizException;
import cn.distributed.transaction.common.exception.enums.BizStatusEnum;
import cn.distributed.transaction.common.util.JsonUtils;
import cn.distributed.transaction.framework.bt.consts.BTConsts;
import cn.distributed.transaction.framework.bt.consts.RedisConsts;
import cn.distributed.transaction.framework.bt.dataobj.UndoLog;
import cn.distributed.transaction.framework.bt.thread.TransactionThreadLocal;
import cn.distributed.transaction.framework.bt.util.BTUtils;
import cn.distributed.transaction.framework.bt.util.CacheTableMeta;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
@Slf4j
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
public class MybatisBTInterceptor implements Interceptor {
    @Autowired
    private RedissonTemplate redissonTemplate;

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if(!TransactionThreadLocal.isTransaction())
            return invocation.proceed();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        if(TransactionThreadLocal.getStatus().equals("1")){
            return invocation.proceed();
        }
        TransactionThreadLocal.addUndoLog();

        Object res;
        switch (mappedStatement.getSqlCommandType()) {
            case INSERT:
                // 处理 INSERT 语句
                handleInsert(invocation,mappedStatement);
                res = invocation.proceed();
                handleAfterInsert();
                break;
            case UPDATE:
                // 处理 UPDATE 语句
                handleUpdate(invocation,mappedStatement);
                res = invocation.proceed();
                break;
            default:
                // 处理 DELETE 语句
                handleDelete(invocation,mappedStatement);
                res = invocation.proceed();
        }
        return res;
    }

    /**
     * 拦截insert,前置镜像不需要，后置镜像仅仅存储idList就行，回滚时根据id删除
     */
    private void handleInsert(Invocation invocation,MappedStatement mappedStatement) {
        String originalSql = resolveSqlWithParameters(invocation);
        String tableName = BTUtils.getInsertSqpTableName(originalSql);
        String key=CacheTableMeta.getTablePrimaryKey(tableName);

        UndoLog undoLog = TransactionThreadLocal.getLastUndoLog();
        undoLog.setTableName(tableName);
        // 打印解析后的 SQL
        log.info("Resolved SQL: {}", originalSql);
        String parseSelectSql = BTUtils.parseSelectSql(originalSql);

        List<Map<String, Object>> orginalLists = SqlRunner.db()
                                                          .selectList(parseSelectSql);

        List<Object> ids = orginalLists.stream()
                                       .map(t ->  t.get(key))
                                       .collect(Collectors.toList());
        undoLog.setLogInfoBefore(JsonUtils.toJsonByte(ids));

    }

    private void handleAfterInsert()  {
        UndoLog undoLog = TransactionThreadLocal.getLastUndoLog();
        String key=CacheTableMeta.getTablePrimaryKey(undoLog.getTableName());
        //更改id
        String sql="select "+key+" from "+undoLog.getTableName();

        List<Map<String, Object>> afterList = SqlRunner.db()
                                                          .selectList(sql);
        List<String> afterIds = afterList.stream()
                                         .map(t -> (String)t.get(key))
                                         .collect(Collectors.toList());

        List beforeIds = JsonUtils.parseObject(undoLog.getLogInfoBefore(),ArrayList.class);


        afterIds.removeAll(beforeIds);

        //进行加锁操作
        afterIds.forEach(t-> redissonTemplate.set(RedisConsts.REDIS_LOCK+":"+t,undoLog.getXid(),RedisConsts.REDIS_LOCK_TIME));
        //向threadLocal放锁
        TransactionThreadLocal.addLocks(afterIds);

        undoLog.setLogInfoBefore(null);
        //undoLog.setLogInfoAfter(JsonUtils.toJsonByte(afterIds));
        String insertAfterImagesSql = BTUtils.getInsertAfterImagesSql(afterIds, undoLog.getTableName(), key);
        undoLog.setLogInfoBefore(JsonUtils.toJsonByte(insertAfterImagesSql));
        undoLog.setCreateTime(LocalDateTime.now());
        undoLog.setUpdateTime(LocalDateTime.now());
        undoLog.setType(BTConsts.TYPE_INSERT);
    }

    /**
     * 获取前置镜像即可，回滚拿前置镜像就行
     */
    @SneakyThrows
    private void handleUpdate(Invocation invocation,MappedStatement mappedStatement)  {
        String originalSql = resolveSqlWithParameters(invocation);
        String tableName = BTUtils.getUpdateSqlTableName(originalSql);
        String[] tableFields = CacheTableMeta.getTableMeta(tableName);
        String key=CacheTableMeta.getTablePrimaryKey(tableName);
        UndoLog undoLog = TransactionThreadLocal.getLastUndoLog();
        undoLog.setTableName(tableName);

        //获得解析后SQL对应的select表示
        String selectSQL = BTUtils.parseUpdateSql(originalSql);

        //获取更新前镜像
        List<Map<String, Object>> orginalLists = SqlRunner.db()
                                                          .selectList(selectSQL);
        //获取镜像ids
        List<String> ids = orginalLists.stream()
                                       .map(t -> (String)t.get(key))
                                       .collect(Collectors.toList());

        //以下两种应该是原子操作，具体实现可以通过Redis LUA实现

        //进行redis查询，是否有数据被其他事物加锁
        for(String id:ids){
            String lockId = redissonTemplate.get(RedisConsts.REDIS_LOCK + "-" + id, String.class);
            if(lockId != null && !lockId.equals(undoLog.getXid()))
                throw new BizException(BizStatusEnum.TRANSACTION_ERROR_COLUMN_EXIST_OTHER_BT);
        }

        //如果数据没有被别的事务链拿到，那么就对这些数据线加锁
        ids.forEach(t-> redissonTemplate.set(RedisConsts.REDIS_LOCK + "-" + t,undoLog.getXid(),RedisConsts.REDIS_LOCK_TIME));

        //然后将这些加锁的ids放入到线程资源中
        TransactionThreadLocal.addLocks(ids);
        //undoLog.setLogInfoBefore(JsonUtils.toJsonByte(orginalLists));
        String json = JsonUtils.toJsonString(orginalLists);
        List<Map<String,Object>> list = JsonUtils.parseObject(json, List.class);
        List<String> updateBeforeImagesSql = BTUtils.getUpdateBeforeImagesSql(list, tableName, tableFields, key);
        undoLog.setLogInfoBefore(JsonUtils.toJsonByte(updateBeforeImagesSql));
        undoLog.setCreateTime(LocalDateTime.now());
        undoLog.setUpdateTime(LocalDateTime.now());
        undoLog.setType(BTConsts.TYPE_UPDATE);
    }

    /**
     * 仅仅获取前置镜像所有数据就可以，但是仍需要判断是否有所，没有后置镜像
     */
    @SneakyThrows
    private void handleDelete(Invocation invocation,MappedStatement mappedStatement) {
        // 处理 DELETE 语句的逻辑
        String originalSql = resolveSqlWithParameters(invocation);

        String tableName = BTUtils.getDeleteSqlTableName(originalSql);
        String key= CacheTableMeta.getTablePrimaryKey(tableName);
        String[] tableFields = CacheTableMeta.getTableMeta(tableName);
        UndoLog undoLog = TransactionThreadLocal.getLastUndoLog();
        undoLog.setTableName(tableName);

        // 打印解析后的 SQL
        log.info("Resolved SQL: {}", originalSql);

        String deleteSql = BTUtils.parseDeleteSql(originalSql);
        List<Map<String, Object>> lists = SqlRunner.db()
                                                   .selectList(deleteSql);
        List<Object> ids = lists.stream()
                                .map(t -> t.get(key))
                                .collect(Collectors.toList());
        //查看是否有锁,但是不需要加锁，因为删除后之后数据别的事务链本身就拿不到
        for(Object id:ids){
            String idS = (String) id;
            String lockId = redissonTemplate.get(RedisConsts.REDIS_LOCK + "-" + idS, String.class);
            if(lockId != null && !lockId.equals(undoLog.getXid()))
                throw new BizException(BizStatusEnum.TRANSACTION_ERROR_COLUMN_EXIST_OTHER_BT);
        }

        TransactionThreadLocal.addLocks(new ArrayList<>());
        undoLog.setLogInfoAfter(null);
        undoLog.setCreateTime(LocalDateTime.now());
        undoLog.setUpdateTime(LocalDateTime.now());
        //undoLog.setLogInfoBefore(JsonUtils.toJsonByte(lists));
        String deleteBeforeImagesSql = BTUtils.getDeleteBeforeImagesSql(lists, tableName, tableFields);
        undoLog.setLogInfoBefore(JsonUtils.toJsonByte(deleteBeforeImagesSql));
        undoLog.setType(BTConsts.TYPE_DELETE);
    }
    @SneakyThrows
    private String resolveSqlWithParameters(Invocation invocation)  {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];

        BoundSql boundSql = mappedStatement.getBoundSql(parameter);

        Configuration configuration = mappedStatement.getConfiguration();
        //获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        //获取当前的sql语句有绑定的所有parameterMapping属性
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //去除空格
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
         /*如果参数满足：org.apache.ibatis.type.TypeHandlerRegistry#hasTypeHandler(java.lang.Class<?>)
                    org.apache.ibatis.type.TypeHandlerRegistry#TYPE_HANDLER_MAP
                    * 即是不是属于注册类型(TYPE_HANDLER_MAP...等/有没有相应的类型处理器)
                     * */

            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                //装饰器，可直接操作属性值 ---》 以parameterObject创建装饰器
                //MetaObject 是 Mybatis 反射工具类，通过 MetaObject 获取和设置对象的属性值
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                //循环 parameterMappings 所有属性
                for (ParameterMapping parameterMapping : parameterMappings) {
                    //获取property属性
                    String propertyName = parameterMapping.getProperty();
                    System.err.println("propertyName: "+propertyName);
                    //是否声明了propertyName的属性和get方法
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        //判断是不是sql的附加参数
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;

    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        }else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }
}
