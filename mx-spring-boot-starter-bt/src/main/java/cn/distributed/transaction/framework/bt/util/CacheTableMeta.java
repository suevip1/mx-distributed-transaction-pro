package cn.distributed.transaction.framework.bt.util;

import java.util.concurrent.ConcurrentHashMap;

public class CacheTableMeta {
    private volatile static ConcurrentHashMap<String,String[]> tableMetaCache = null;
    private volatile static ConcurrentHashMap<String,String> tablePrimaryKey =null;

    private volatile static ConcurrentHashMap<String,String[]> tableFieldsType = null;

    public static void addTableMeta(String tableName,String[] fields){
        if(tableMetaCache==null){
            synchronized (CacheTableMeta.class){
                if(tableMetaCache==null){
                    tableMetaCache=new ConcurrentHashMap<>();
                }
            }
        }
        tableMetaCache.put(tableName,fields);
    }

    public static void addTableFieldsType(String tableName,String[] type){
        if(tableFieldsType==null){
            synchronized (CacheTableMeta.class){
                if(tableFieldsType==null){
                    tableFieldsType=new ConcurrentHashMap<>();
                }
            }
        }
        tableFieldsType.put(tableName,type);
    }

    public static void addTablePrimaryKey(String tableName,String primaryKey){
        if(tablePrimaryKey==null){
            synchronized (CacheTableMeta.class){
                if(tablePrimaryKey==null){
                    tablePrimaryKey=new ConcurrentHashMap<>();
                }
            }
        }
        tablePrimaryKey.put(tableName,primaryKey);
    }

    public static String[] getTableMeta(String tableName){
        return tableMetaCache.getOrDefault(tableName,null) ;
    }

    public static String getTablePrimaryKey(String tableName){

        return tablePrimaryKey.getOrDefault(tableName,null);
    }

    public static String[] getTableFieldsType(String tableName){
        return tableFieldsType.getOrDefault(tableName,null);
    }
}
