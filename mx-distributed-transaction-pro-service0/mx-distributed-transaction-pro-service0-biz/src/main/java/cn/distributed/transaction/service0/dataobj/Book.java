package cn.distributed.transaction.service0.dataobj;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@TableName("book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private String id;

    private String name;

    private Integer cost;

    private Integer number;
}
