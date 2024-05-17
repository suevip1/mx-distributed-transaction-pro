package cn.distributed.transaction.framework.tx.thread;

import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class TransactionThreadLocal {
    private static ThreadLocal<String> xid = new ThreadLocal<>();
    private static ThreadLocal<List<BTLocal>> btLocals = new ThreadLocal<>();

    private static ThreadLocal<Boolean> isTransaction = new ThreadLocal<>();

    public static void init(){
        xid.set(IdUtil.randomUUID());
        btLocals.set(new ArrayList<>());
        isTransaction.set(true);
    }

    public static boolean isTransaction(){
        if(isTransaction.get()!=null&&isTransaction.get().equals(Boolean.TRUE))
            return true;
        return false;
    }

    public static void addBTLocal(String bid){
        btLocals.get().add(new BTLocal(bid));
    }


    public static void updateLastBTLocal(String locksList,String host,Integer port){
        BTLocal btLocal = getLastBTLocal();
        btLocal.setLocksList(locksList);
        btLocal.setPort(port);
        btLocal.setHost(host);
    }

    public static String getXid(){
        return xid.get();
    }

    public static void remove(){
        xid.remove();
        btLocals.remove();
    }

    public static BTLocal getLastBTLocal(){
        List<BTLocal> locals = btLocals.get();
        return locals.get(locals.size()-1);
    }

    public static List<BTLocal> getBTLocals(){
        return btLocals.get();
    }
    @Data
    @NoArgsConstructor
    public static class BTLocal{
        private String bid;
        private String locksList;
        private Integer port;
        private String host;

        public BTLocal(String bid) {
            this.bid = bid;
        }
    }
}
