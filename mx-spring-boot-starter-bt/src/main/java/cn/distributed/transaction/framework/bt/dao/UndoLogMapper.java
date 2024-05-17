package cn.distributed.transaction.framework.bt.dao;

import cn.distributed.transaction.framework.bt.dataobj.UndoLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UndoLogMapper extends BaseMapper<UndoLog> {
    public void insertUndoLog( UndoLog undoLog);

    public UndoLog selectUndoLog(@Param("xid") String xid, @Param("branchId") String branchId);

    public void updateUndoLogStatus(@Param("xid") String xid,@Param("branchId") String branchId);

    public List<UndoLog> getAllRollbackData();

    public void updateRollbackUndoLogStatus(@Param("xid") String xid,@Param("branchId") String branchId);
}
