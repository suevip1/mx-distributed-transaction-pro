package cn.distributed.transaction.service1.api;



import cn.distributed.transaction.common.res.RestRes;
import cn.distributed.transaction.service1.api.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = Service1BookApi.SERVERNAME)
public interface Service1BookApi {
    static final String SERVERNAME = "service1";
    static final String PREFIX = "/api/service1/book";

    @PostMapping("/feign"+PREFIX+"/addBooks")
    public RestRes<?> addBooks(@RequestBody List<BookDto> bookDtoList);

    @PostMapping("/feign"+PREFIX+"/deleteBook")
    public RestRes<?> deleteBook(@RequestParam("id") String id);

    @PostMapping("/feign"+PREFIX+"/addBook")
    public RestRes<?> addBook(@RequestBody BookDto bookDto);
    @PostMapping("/feign"+PREFIX+"/multiUpdate")
    public RestRes<?> multiUpdate();


}
