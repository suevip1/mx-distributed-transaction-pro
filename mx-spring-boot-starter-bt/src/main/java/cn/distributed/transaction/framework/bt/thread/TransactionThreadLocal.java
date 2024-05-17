package cn.distributed.transaction.framework.bt.thread;

import cn.distributed.transaction.framework.bt.dataobj.UndoLog;
import cn.distributed.transaction.framework.bt.enums.UndoLogStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class TransactionThreadLocal {
    private static ThreadLocal<BTThreadLocal> btThreadLocal = new ThreadLocal<>();

    private static ThreadLocal<String> btStatus=new ThreadLocal<>();

    private static ThreadLocal<String> btId=new ThreadLocal<>();

    private static ThreadLocal<Integer> rollbackPort = new ThreadLocal<Integer>();

    private static ThreadLocal<List<UndoLog>> undoLogThreadLocal = new ThreadLocal<>();

    private static ThreadLocal<Boolean> isTransaction = new ThreadLocal<>();

    private static ThreadLocal<Boolean> isSuccess = new ThreadLocal<>();


    public static void init(String xid,String bid,Integer port){
        btThreadLocal.set(new BTThreadLocal(xid,bid,new ArrayList<>()));
        btStatus.set("0");
        btId.set(xid+"-"+bid+"-0");
        undoLogThreadLocal.set(new ArrayList<>());
        rollbackPort.set(port);
        isTransaction.set(Boolean.TRUE);
        isSuccess.set(Boolean.TRUE);
    }

    public static void errorTransaction(){
        isSuccess.set(Boolean.FALSE);
    }

    public static boolean isTransaction(){
        if(isTransaction.get()!=null&&isTransaction.get().equals(Boolean.TRUE))
            return true;
        return false;
    }

    public static boolean isSuccess(){
        return isSuccess.get()!=null&&isSuccess.get().equals(Boolean.TRUE);
    }

    public static Integer getPort(){
        return rollbackPort.get();
    }

    public static void addLocks(List<String> locks){
        btThreadLocal.get().add(locks);
    }

    public static void updateBTId(){
        String lastBId = btId.get();
        String[] split = lastBId.split("-");
        int id = Integer.parseInt(split[split.length - 1]) + 1;
        btId.set(btThreadLocal.get().getXid()+"-"+btThreadLocal.get().getBid()+"-"+id);
    }

    public static void addUndoLog(){
        UndoLog undoLog = new UndoLog();
        undoLog.setXid(btThreadLocal.get().getXid());
        undoLog.setBranchId(btId.get());
        undoLog.setLogStatus(UndoLogStatus.intermediateStatus.getValue());
        undoLogThreadLocal.get().add(undoLog);
        updateBTId();
    }

    public static UndoLog getLastUndoLog(){
        List<UndoLog> undoLogs = undoLogThreadLocal.get();
        return undoLogs.get(undoLogs.size()-1);
    }

    public static void updateStatus(){
        btStatus.set("1");
    }

    public static String getStatus(){
        return btStatus.get();
    }

    public static BTThreadLocal getBTThreadLocal(){
        return btThreadLocal.get();
    }

    public static List<UndoLog> getUndoLogListThreadLocal(){
        return undoLogThreadLocal.get();
    }

    public static void remove(){
        btThreadLocal.remove();
        btStatus.remove();
        btId.remove();
        undoLogThreadLocal.remove();
        rollbackPort.remove();
        isSuccess.remove();
        isTransaction.remove();
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BTThreadLocal{
        private String xid;

        private String bid;

        private List<List<String>> locksList;


        public void add(List<String> locks){
            this.locksList.add(locks);
        }
    }
}
