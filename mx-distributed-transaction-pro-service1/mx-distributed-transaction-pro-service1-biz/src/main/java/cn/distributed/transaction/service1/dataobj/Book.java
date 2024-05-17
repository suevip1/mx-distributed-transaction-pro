package cn.distributed.transaction.service1.dataobj;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
