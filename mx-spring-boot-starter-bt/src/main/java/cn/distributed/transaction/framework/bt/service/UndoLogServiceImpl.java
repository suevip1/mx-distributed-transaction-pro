package cn.distributed.transaction.framework.bt.service;

import cn.distributed.transaction.common.util.JsonUtils;
import cn.distributed.transaction.framework.bt.config.BTTableMetaConfig;
import cn.distributed.transaction.framework.bt.dao.UndoLogMapper;
import cn.distributed.transaction.framework.bt.dataobj.UndoLog;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UndoLogServiceImpl implements UndoLogService{
    @Autowired
    DruidDataSource druidDataSource;

    @Autowired
    BTTableMetaConfig btTableMetaConfig;

    private final UndoLogMapper undoLogMapper;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void handlerInsert(UndoLog undoLog){
        byte[] logInfoBefore = undoLog.getLogInfoBefore();
        String sql = JsonUtils.parseObject(logInfoBefore, String.class);
        Statement statement = druidDataSource.getConnection()
                                             .createStatement();
        statement.execute(sql);
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void handlerDelete(UndoLog undoLog){
        byte[] logInfoBefore = undoLog.getLogInfoBefore();
        String sql = JsonUtils.parseObject(logInfoBefore, String.class);
        Statement statement = druidDataSource.getConnection()
                                             .createStatement();
        statement.execute(sql);
    }
    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void handlerUpdate(UndoLog undoLog){
        byte[] logInfoBefore = undoLog.getLogInfoBefore();
        Statement statement = druidDataSource.getConnection()
                                             .createStatement();

        List<String> sql = JsonUtils.parseObject(logInfoBefore, List.class);
        sql.forEach(t-> {
            try {
                statement.execute(t);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<UndoLog> getAllRollbackData() {
        List<UndoLog> rollbackData = undoLogMapper.getAllRollbackData();
        return rollbackData;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRollbackUndoLogStatus(UndoLog undoLog) {
        undoLogMapper.updateRollbackUndoLogStatus(undoLog.getXid(),undoLog.getBranchId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommonUndoStatus(String xid,String bid) {
        undoLogMapper.updateUndoLogStatus(xid,bid);
    }

    @Override
    public void insertUndoLog(UndoLog undoLog) {
        undoLogMapper.insertUndoLog(undoLog);
    }

    @Override
    public UndoLog getUndoLogByXidBid(String xid, String bid) {
        return undoLogMapper.selectUndoLog(xid,bid);
    }

}
