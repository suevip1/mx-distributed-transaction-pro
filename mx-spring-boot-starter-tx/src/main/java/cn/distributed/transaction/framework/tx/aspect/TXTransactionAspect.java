package cn.distributed.transaction.framework.tx.aspect;

import cn.distributed.transaction.framework.tx.client.ClientTcpServer;
import cn.distributed.transaction.framework.tx.thread.TransactionThreadLocal;

import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class TXTransactionAspect {
    @Autowired
    ClientTcpServer clientTcpServer;
    @Pointcut("@annotation(cn.distributed.transaction.framework.tx.annocation.TXTransaction)")
    public void txTransactionPointcut(){}

    @Before("txTransactionPointcut()")
    public void preHandleTXTransaction() throws Throwable{
        TransactionThreadLocal.init();
    }

    @AfterReturning(pointcut = "txTransactionPointcut()", returning = "result")
    public void suffixHandlerTXTransaction(Object result){
        clientTcpServer.handleCommonTransaction();
        TransactionThreadLocal.remove();
    }

    @AfterThrowing(pointcut = "txTransactionPointcut()", throwing = "ex") // 执行失败
    public void afterThrowing(Throwable ex) {
        //回滚操作
        clientTcpServer.handleRollbackTransaction();
        TransactionThreadLocal.remove();
    }
}
