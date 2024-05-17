package cn.distributed.transaction.service0.api;

import cn.distributed.transaction.common.res.RestRes;
import cn.distributed.transaction.service0.api.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = Service0BookApi.SERVERNAME)
public interface Service0BookApi {
    static final String SERVERNAME = "service0";
    static final String PREFIX = "/api/service0/book";

    @PostMapping("/feign"+PREFIX+"/addBooks")
    public RestRes<?> addBooks(@RequestBody List<BookDto> bookDtoList);

    @PostMapping("/feign"+PREFIX+"/deleteBook")
    public RestRes<?> deleteBook(@RequestParam("id") String id);

    @PostMapping("/feign"+PREFIX+"/addBook")
    public RestRes<?> addBook(@RequestBody BookDto bookDto);
    @PostMapping("/feign"+PREFIX+"/multiUpdate")
    public RestRes<?> multiUpdate();
}
