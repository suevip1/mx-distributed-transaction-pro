package cn.distributed.transaction.framework.tx.config;


import cn.distributed.transaction.framework.tx.consts.TXConsts;
import cn.distributed.transaction.framework.tx.thread.TransactionThreadLocal;
import cn.hutool.core.util.IdUtil;
import feign.Feign;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;



@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String bid = IdUtil.randomUUID();
        template.header(TXConsts.TX_REQUEST_HEADER_XID, TransactionThreadLocal.getXid());
        template.header(TXConsts.TX_REQUEST_HEADER_BID, bid);
        TransactionThreadLocal.addBTLocal(bid);

        Request request = template.request();
    }

}
