package cn.distributed.transaction.framework.bt.util;


import com.baomidou.mybatisplus.annotation.TableField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTUtils {

    public static String parseUpdateSql(String sql){
        String[] split = sql.split("\\s+");
        StringBuilder afterParse = new StringBuilder("select * from "+split[1]);
        for(int i=2;i<split.length;i++){
            if(split[i].equalsIgnoreCase("where")){
                afterParse.append(" "+split[i++]);
                for(;i<split.length;i++)
                    afterParse.append(" "+split[i]);
            }
        }
        return afterParse.toString();
    }

    public static String parseSelectSql(String sql){
        String[] split = sql.split("\\s+");
        return "select id from "+split[2];
    }

    public static String parseDeleteSql(String sql){
        String[] split = sql.split("\\s+");
        StringBuilder stringBuilder = new StringBuilder(split[1]);
        for(int i=2;i<split.length;i++)
            stringBuilder.append(" "+split[i]);
        return "select * "+stringBuilder.toString();
    }

    public static String getInsertAfterImagesSql(List<?> keys,String tableName,String primaryKey){
        StringBuilder sql = new StringBuilder("delete from " + tableName + " where " + primaryKey +" in (");
        for(int i=0;i<keys.size();i++)
            if(i!=keys.size()-1)
                sql.append(keys.get(i)+",");
            else sql.append(keys.get(i)+")");
        return sql.toString();
    }

    public static String getDeleteBeforeImagesSql(List<Map<String,Object>> list, String tableName, String[] tableFields){
        StringBuilder sql=new StringBuilder("insert into "+tableName+" (");
        for(int i=0;i<tableFields.length;i++)
            if(i!=tableFields.length-1)
                sql.append(tableFields[i]+",");
            else sql.append(tableFields[i]+") values(");
        int index=0;
        list.forEach(t->{
            for(int i=0;i<tableFields.length;i++)
                if(i!=tableFields.length-1)
                    sql.append(t.get(tableFields[i])+",");
                else sql.append(t.get(tableFields[i])+")");
        });
        return sql.toString();
    }

    public static List<String> getUpdateBeforeImagesSql(List<Map<String,Object>> list,String tableName,String[] tableFields,String primary){
        ArrayList<String> sqlList = new ArrayList<>();
        list.forEach(t->{
            StringBuilder sql = new StringBuilder("update " + tableName + " set ");
            for(int i=0;i<tableFields.length;i++)
                if(i!=tableFields.length-1)
                    sql.append(tableFields[i]+" = "+t.get(tableFields[i])+",");
                else sql.append(tableFields[i]+" = "+t.get(tableFields[i])+" where "+primary+" = "+t.get(primary));
            sqlList.add(sql.toString());
        });
        return sqlList;
    }

    public static String getInsertSqpTableName(String sql){
        return sql.split("\\s+")[2];
    }

    public static String getDeleteSqlTableName(String sql){
        return sql.split("\\s+")[2];
    }

    public static String getUpdateSqlTableName(String sql){
        return sql.split("\\s+")[1];
    }


}
