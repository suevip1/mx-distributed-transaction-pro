package cn.distributed.transaction.framework.bt.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PreparedStatementSetter {

    // 创建映射表，关联SQL数据类型和对应的设置方法
    private static final Map<String, SetterFunction> TYPE_SETTER_MAP = new HashMap<>();

    // 定义SetterFunction接口，用于调用PreparedStatement的方法
    interface SetterFunction {
        void setParameter(PreparedStatement preparedStatement, int parameterIndex, Object value) throws SQLException;
    }

    static {
        // 数值类型
        TYPE_SETTER_MAP.put("TINYINT", (ps, index, value) -> ps.setByte(index, (Byte) value));
        TYPE_SETTER_MAP.put("SMALLINT", (ps, index, value) -> ps.setShort(index, (Short) value));
        TYPE_SETTER_MAP.put("INT", (ps, index, value) -> ps.setInt(index, (Integer) value));
        TYPE_SETTER_MAP.put("BIGINT", (ps, index, value) -> ps.setLong(index, (Long) value));
        TYPE_SETTER_MAP.put("FLOAT", (ps, index, value) -> ps.setFloat(index, (Float) value));
        TYPE_SETTER_MAP.put("DOUBLE", (ps, index, value) -> ps.setDouble(index, (Double) value));
        TYPE_SETTER_MAP.put("DECIMAL", (ps, index, value) -> ps.setBigDecimal(index, (java.math.BigDecimal) value));

        // 字符串类型
        TYPE_SETTER_MAP.put("CHAR", (ps, index, value) -> ps.setString(index, (String) value));
        TYPE_SETTER_MAP.put("VARCHAR", (ps, index, value) -> ps.setString(index, (String) value));
        TYPE_SETTER_MAP.put("TEXT", (ps, index, value) -> ps.setString(index, (String) value));
        TYPE_SETTER_MAP.put("BLOB", (ps, index, value) -> ps.setBytes(index, (byte[]) value));

        // 日期和时间类型
        TYPE_SETTER_MAP.put("DATE", (ps, index, value) -> ps.setDate(index, (java.sql.Date) value));
        TYPE_SETTER_MAP.put("TIME", (ps, index, value) -> ps.setTime(index, (java.sql.Time) value));
        TYPE_SETTER_MAP.put("DATETIME", (ps, index, value) -> ps.setTimestamp(index, (java.sql.Timestamp) value));
        TYPE_SETTER_MAP.put("TIMESTAMP", (ps, index, value) -> ps.setTimestamp(index, (java.sql.Timestamp) value));

        // 布尔类型
        TYPE_SETTER_MAP.put("BOOLEAN", (ps, index, value) -> ps.setBoolean(index, (Boolean) value));
    }

    // 设置PreparedStatement参数
    public static void setParameter(PreparedStatement preparedStatement, int parameterIndex, String dataType, Object value) {
        try {
            SetterFunction setter = TYPE_SETTER_MAP.get(dataType.toUpperCase());
            setter.setParameter(preparedStatement, parameterIndex, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

