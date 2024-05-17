package cn.distributed.transaction.service0.api;

import cn.distributed.transaction.common.res.RestRes;
import cn.distributed.transaction.service0.api.dto.BookDto;
import cn.distributed.transaction.service0.dataobj.Book;
import cn.distributed.transaction.service0.service.BookService;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class Serice0BookApiImpl implements Service0BookApi{
    private final BookService bookService;

    @Override
    public RestRes<?> addBooks(List<BookDto> bookDtoList) {
        List<Book> bookList = bookDtoList.stream()
                                        .map(t -> {
                                            Book book = new Book();
                                            BeanUtil.copyProperties(t, book);
                                            return book;
                                        })
                                        .collect(Collectors.toList());
        return bookService.batchInserts(bookList);
    }

    @Override
    public RestRes<?> deleteBook(String id) {
        if(!id.equals(" "))
            throw new RuntimeException("测试回滚有效性");
        return bookService.deleteBookById(id);
    }

    @Override
    public RestRes<?> addBook(BookDto bookDto) {
        Book book = new Book();
        BeanUtil.copyProperties(bookDto,book);
        return bookService.addOBook(book);
    }

    @Override
    public RestRes<?> multiUpdate() {
        return bookService.multiUpadte();
    }
}
