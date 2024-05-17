package cn.distributed.transaction.framework.bt.config;

import cn.distributed.transaction.framework.bt.consts.BTConsts;
import cn.distributed.transaction.framework.bt.dataobj.UndoLog;
import cn.distributed.transaction.framework.bt.properties.BTProperties;
import cn.distributed.transaction.framework.bt.service.UndoLogService;
import cn.distributed.transaction.framework.bt.thread.TransactionThreadLocal;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@Component
@Order(1)
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final UndoLogService undoLogService;
    private final BTProperties btProperties;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(TransactionThreadLocal.isTransaction()&&TransactionThreadLocal.isSuccess())
            updateResponseHeader(response);
        return body;
    }

    private void updateResponseHeader(ServerHttpResponse response) {
        TransactionThreadLocal.updateStatus();
        Integer port = TransactionThreadLocal.getPort();
        List<UndoLog> undoLogThreadLocal = TransactionThreadLocal.getUndoLogListThreadLocal();
        undoLogThreadLocal.forEach(undoLogService::insertUndoLog);
        TransactionThreadLocal.BTThreadLocal btThreadLocal = TransactionThreadLocal.getBTThreadLocal();
        List<List<String>> locksList = btThreadLocal.getLocksList();

        response.getHeaders().add(BTConsts.BT_RESPONSE_HEADER_PORT, String.valueOf(port));
        response.getHeaders().add(BTConsts.BT_RESPONSE_HEADER_SUCCESS, "true");
        response.getHeaders().add(BTConsts.BT_RESPONSE_HEADER_LOCKS, JSONUtil.toJsonStr(locksList));
        response.getHeaders().add(BTConsts.BT_RESPONSE_HEADER_HOST, btProperties.getHost());

    }
}
