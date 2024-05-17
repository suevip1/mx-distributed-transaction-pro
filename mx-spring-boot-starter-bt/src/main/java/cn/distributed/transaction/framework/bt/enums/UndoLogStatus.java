package cn.distributed.transaction.framework.bt.enums;

import lombok.Getter;

@Getter
public enum UndoLogStatus {
    intermediateStatus(0,"事务链执行中间态"),
    rollbackStatus(1,"事务链执行回滚"),

    commonStatus(2,"事务链正常处理完成");
    private UndoLogStatus(Integer value,String desc){
        this.desc=desc;
        this.value=value;
    }

    private Integer value;

    private String desc;
}
