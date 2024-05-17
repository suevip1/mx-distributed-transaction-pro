package cn.distributed.transaction.service0.service.impl;

import cn.distributed.transaction.common.res.RestRes;
import cn.distributed.transaction.service0.api.dto.BookDto;
import cn.distributed.transaction.service0.dataobj.Book;
import cn.distributed.transaction.service0.dao.BookMapper;
import cn.distributed.transaction.service0.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookMapper bookMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes addOBook(Book book) {
        bookMapper.insertBook(book);
        return RestRes.ok();
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes updateBook(Book book){
        bookMapper.updateById(book);
        return RestRes.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes batchInserts(List<Book> list) {
        bookMapper.batchInsert(list);
        return RestRes.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes multiUpadte() {
        ArrayList<Book> books = new ArrayList<>();
        books.add(new Book("9911","00",2,10));
        books.add(new Book("9912","00",2,10));
        books.add(new Book("9913","00",2,10));
        books.add(new Book("9914","00",2,10));
        bookMapper.batchInsert(books);

        bookMapper.deleteById("9911");
        return RestRes.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public RestRes deleteBookById(String id) {
        bookMapper.deleteById(id);
        return RestRes.ok();
    }
}
