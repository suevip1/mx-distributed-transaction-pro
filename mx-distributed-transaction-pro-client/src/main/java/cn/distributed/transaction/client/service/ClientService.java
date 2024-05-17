package cn.distributed.transaction.client.service;

import cn.distributed.transaction.common.res.RestRes;
import cn.distributed.transaction.service0.api.dto.BookDto;

public interface ClientService {
    public RestRes<Void> bookTransaction();

}
