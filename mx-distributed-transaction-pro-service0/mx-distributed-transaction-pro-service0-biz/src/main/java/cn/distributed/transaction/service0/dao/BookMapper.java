package cn.distributed.transaction.service0.dao;



import cn.distributed.transaction.service0.dataobj.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    @Insert("insert into book values(#{id},#{name},#{cost},#{number})")
    public void insertBook( Book book);

    @Select("select id from book ")
    public List<String> selectIds();
    @Insert("<script>insert into book values <foreach item = 'item' collection = 'books' separator = ','>" +
            "(#{item.id},#{item.name},#{item.cost},#{item.number})</foreach></script>")
    public void batchInsert(@Param("books") List<Book> books);
    @Update("update book set number = #{aNumber} where number = #{bNumber}")
    public void updateNumber(@Param("bNumber") Integer bNumber,@Param("aNumber") Integer aNumber);
}
