package cn.distributed.transaction.service1.service;

import cn.distributed.transaction.common.res.RestRes;
import cn.distributed.transaction.service1.dataobj.Book;

import java.util.List;


public interface BookService {
    public RestRes addOBook(Book book);

    public RestRes deleteBookById(String id);

    public RestRes updateBook(Book book);

    public RestRes batchInserts(List<Book> list);

    public RestRes multiUpadte();
}
