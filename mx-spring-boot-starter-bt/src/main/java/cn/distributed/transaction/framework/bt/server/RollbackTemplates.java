package cn.distributed.transaction.framework.bt.server;

import cn.distributed.transaction.framework.bt.config.RedissonTemplate;
import cn.distributed.transaction.framework.bt.consts.BTConsts;
import cn.distributed.transaction.framework.bt.dataobj.UndoLog;
import cn.distributed.transaction.framework.bt.dto.RollbackDto;
import cn.distributed.transaction.framework.bt.service.UndoLogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class RollbackTemplates {

    @Autowired
    UndoLogService undoLogService;

    @Autowired
    RedissonTemplate redissonTemplate;
    public void rollback(RollbackDto rollbackDto){
        String xid = rollbackDto.getXid();
        String bid = rollbackDto.getBid();
        List<List<String>> locks = rollbackDto.getLocks();
        for(int i=locks.size()-1;i>=0;i--){
            UndoLog undoLog = undoLogService.getUndoLogByXidBid(xid,xid+"-"+bid+"-"+i);
            handleRollback(undoLog);
        }
        locks.forEach(t->t.forEach(k->redissonTemplate.delete(k)));
    }

    public void rollbackInitData(){
        List<UndoLog> rollbackData = undoLogService.getAllRollbackData();
        for(int i=rollbackData.size()-1;i>=0;i--){
            UndoLog undoLog = rollbackData.get(i);
            handleRollback(undoLog);
            undoLogService.updateCommonUndoStatus(undoLog.getXid(),undoLog.getBranchId());
        }
    }

    public void commonExecute(RollbackDto rollbackDto){
        String xid = rollbackDto.getXid();
        String bid = rollbackDto.getBid();
        List<List<String>> locks = rollbackDto.getLocks();
        for(int i=locks.size()-1;i>=0;i--){
            undoLogService.updateCommonUndoStatus(xid,xid+"-"+bid+"-"+i);
        }
        locks.forEach(t->t.forEach(k->redissonTemplate.delete(k)));
    }

    private void handleRollback(UndoLog undoLog){
        String type=undoLog.getType();
        switch (type){
            case BTConsts.TYPE_INSERT:
                undoLogService.handlerInsert(undoLog);
                break;
            case BTConsts.TYPE_UPDATE:
                undoLogService.handlerUpdate(undoLog);
                break;
            default:
                undoLogService.handlerDelete(undoLog);
                break;
        }
        undoLogService.updateRollbackUndoLogStatus(undoLog);
    }
}
