package cn.distributed.transaction.client.service.impl;

import cn.distributed.transaction.client.service.ClientService;
import cn.distributed.transaction.common.res.RestRes;
import cn.distributed.transaction.framework.tx.annocation.TXTransaction;
import cn.distributed.transaction.service0.api.Service0BookApi;
import cn.distributed.transaction.service0.api.dto.BookDto;

import cn.distributed.transaction.service1.api.Service1BookApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final Service0BookApi service0BookApi;
    private final Service1BookApi service1BookApi;

    @Override
    @TXTransaction
    public RestRes<Void> bookTransaction() {
        service0BookApi.multiUpdate();

        service1BookApi.addBook(new cn.distributed.transaction.service1.api.dto.BookDto("9999","hh",10,10));
        return RestRes.ok();
    }
}
