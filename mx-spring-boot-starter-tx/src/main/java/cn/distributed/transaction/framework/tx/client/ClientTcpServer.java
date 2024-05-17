package cn.distributed.transaction.framework.tx.client;

import cn.distributed.transaction.common.util.JsonUtils;
import cn.distributed.transaction.framework.tx.consts.ClientConsts;
import cn.distributed.transaction.framework.tx.consts.TXConsts;
import cn.distributed.transaction.framework.tx.dto.RollbackDto;
import cn.distributed.transaction.framework.tx.thread.TransactionThreadLocal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

@Component
@Slf4j
@ConditionalOnProperty(TXConsts.TX_ENABLE)
public class ClientTcpServer {
    //事务链结束处理应该逆序进行,下标从倒数第二个开始是因为最后一个分支在自身业务执行时就已经回滚，undo_log表未有对应事务行数据
    public void handleRollbackTransaction(){
        List<TransactionThreadLocal.BTLocal> btLocals = TransactionThreadLocal.getBTLocals();
        for(int i=btLocals.size()-2;i>=0;i--)
            socketHandler(btLocals.get(i),ClientConsts.ROLLBACK_TRANSACTION_EXECUTE);
    }

    public void handleCommonTransaction(){
        List<TransactionThreadLocal.BTLocal> btLocals = TransactionThreadLocal.getBTLocals();
        for(int i=btLocals.size()-1;i>=0;i--)
            socketHandler(btLocals.get(i),ClientConsts.COMMON_TRANSACTION_EXECUTE);
    }


    private void socketHandler(TransactionThreadLocal.BTLocal t,String type) {
        String xid = TransactionThreadLocal.getXid();
        String bid= t.getBid();
        String locksStr = t.getLocksList();
        List<List<String>> locksList = JsonUtils.parseObject(locksStr, List.class);
        RollbackDto rollbackDto = new RollbackDto(xid,bid,locksList);
        Socket socket = clientConnect(t.getHost(), t.getPort());
        log.info(ClientConsts.BUILD_CONNECT+" "+t.getHost()+":"+t.getPort());
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(JsonUtils.toJsonString(rollbackDto));
            out.println(type);
            //等待服务端发送处理完毕
            while(!(in.readLine()).equals(ClientConsts.FINISH_CONNECT)){}
            socket.close();

            log.info(ClientConsts.FINISH_CONNECT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private Socket clientConnect(String host,Integer port){
        return new Socket(host,port);
    }
}
