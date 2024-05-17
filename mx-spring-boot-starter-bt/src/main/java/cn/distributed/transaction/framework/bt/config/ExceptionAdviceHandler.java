package cn.distributed.transaction.framework.bt.config;


import cn.distributed.transaction.common.res.RestRes;
import cn.distributed.transaction.framework.bt.thread.TransactionThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
@Order(0)
@Component
public class ExceptionAdviceHandler {
    @ExceptionHandler(RuntimeException.class)
    public RestRes<Void> handlerRuntimeException(RuntimeException runtimeException){
        log.error("biz error:{} msg:{}",runtimeException,runtimeException.getMessage());
        if(TransactionThreadLocal.isTransaction()) {
            TransactionThreadLocal.errorTransaction();
            return RestRes.error(runtimeException.getMessage(),301);
        }
        return RestRes.error(runtimeException.getMessage(),302);
    }
}
