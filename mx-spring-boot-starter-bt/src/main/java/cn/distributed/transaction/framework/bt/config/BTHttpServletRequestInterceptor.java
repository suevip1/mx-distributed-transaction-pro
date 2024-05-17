package cn.distributed.transaction.framework.bt.config;


import cn.distributed.transaction.framework.bt.consts.BTConsts;
import cn.distributed.transaction.framework.bt.properties.BTProperties;
import cn.distributed.transaction.framework.bt.thread.TransactionThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BTHttpServletRequestInterceptor implements HandlerInterceptor {
    @Autowired
    BTProperties btProperties;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String xid = request.getHeader(BTConsts.BT_REQUEST_HEADER_XID);
        String bid = request.getHeader(BTConsts.BT_REQUEST_HEADER_BID);
        TransactionThreadLocal.init(xid,bid,btProperties.getRollbackPort());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 所有请求都要进行缓存清理
        TransactionThreadLocal.remove();
    }
}
