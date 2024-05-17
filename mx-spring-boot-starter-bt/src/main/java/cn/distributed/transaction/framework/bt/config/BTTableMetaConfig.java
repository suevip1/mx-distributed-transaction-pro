package cn.distributed.transaction.framework.bt.config;

import cn.distributed.transaction.framework.bt.consts.ServerConsts;
import cn.distributed.transaction.framework.bt.util.CacheTableMeta;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;



public class BTTableMetaConfig {
    @Autowired
    private DruidDataSource druidDataSource;

    @PostConstruct
    @SneakyThrows
    public void initCacheTableMete(){
        DruidPooledConnection connection = druidDataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tablesResultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});

        while (tablesResultSet.next()) {
            String tableName = tablesResultSet.getString("TABLE_NAME");
            ArrayList<String> fieldsNameList = new ArrayList<>();
            ArrayList<String> fieldTypeList = new ArrayList<>();
            // 获取表的主键
            ResultSet primaryKeysResultSet = metaData.getPrimaryKeys(null, null, tableName);

            while (primaryKeysResultSet.next()) {
                String primaryKeyColumn = primaryKeysResultSet.getString("COLUMN_NAME");
                CacheTableMeta.addTablePrimaryKey(tableName,primaryKeyColumn);
            }

            // 获取表的字段信息
            ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);
            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String dataType = columnsResultSet.getString("DATA_TYPE");
                fieldsNameList.add(columnName);
                fieldTypeList.add(dataType);
            }
            CacheTableMeta.addTableMeta(tableName,fieldsNameList.toArray(new String[]{}));
            CacheTableMeta.addTableFieldsType(tableName,fieldTypeList.toArray(new String[]{}));
        }
    }
}
