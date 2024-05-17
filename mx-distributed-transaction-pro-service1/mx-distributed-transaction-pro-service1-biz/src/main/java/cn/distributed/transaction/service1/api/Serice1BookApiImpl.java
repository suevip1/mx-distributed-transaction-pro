package cn.distributed.transaction.service1.api;

import cn.distributed.transaction.common.res.RestRes;

import cn.distributed.transaction.service1.api.dto.BookDto;
import cn.distributed.transaction.service1.dataobj.Book;
import cn.distributed.transaction.service1.service.BookService;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class Serice1BookApiImpl implements Service1BookApi{
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
