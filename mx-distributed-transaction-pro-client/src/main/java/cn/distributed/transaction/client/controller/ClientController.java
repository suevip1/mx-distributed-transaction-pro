package cn.distributed.transaction.client.controller;

import cn.distributed.transaction.client.service.ClientService;
import cn.distributed.transaction.common.res.RestRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping("test")
    public RestRes<Void> test(){
        return clientService.bookTransaction();
    }
}
