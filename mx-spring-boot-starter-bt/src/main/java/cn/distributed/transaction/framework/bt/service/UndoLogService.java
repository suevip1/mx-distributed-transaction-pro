package cn.distributed.transaction.framework.bt.service;

import cn.distributed.transaction.framework.bt.dataobj.UndoLog;

import java.util.List;

public interface UndoLogService {

    public void insertUndoLog(UndoLog undoLog);

    public UndoLog getUndoLogByXidBid(String xid,String bid);

    public void handlerInsert(UndoLog undoLog);

    public void handlerDelete(UndoLog undoLog);

    public void handlerUpdate(UndoLog undoLog);

    public List<UndoLog> getAllRollbackData();

    public void updateRollbackUndoLogStatus(UndoLog undoLog);

    public void updateCommonUndoStatus(String xid,String bid);
}
