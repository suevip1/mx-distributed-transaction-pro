package cn.distributed.transaction.framework.bt.dataobj;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName(value = "undo_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UndoLog {

    private String xid;

    private String branchId;
    private Integer logStatus;
    private byte[] logInfoBefore;
    private byte[] logInfoAfter;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String tableName;

    private String type;
}