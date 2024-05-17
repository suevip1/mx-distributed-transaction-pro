package cn.distributed.transaction.framework.bt.server;

import cn.distributed.transaction.common.util.JsonUtils;
import cn.distributed.transaction.framework.bt.consts.ServerConsts;
import cn.distributed.transaction.framework.bt.dto.RollbackDto;
import cn.distributed.transaction.framework.bt.properties.BTProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j

public class RollbackServer {
    @Autowired
    BTProperties btProperties;

    @Autowired
    RollbackTemplates rollbackTemplates;

    private static volatile ThreadPoolExecutor tcpConnectThreadPool = null;

    @PostConstruct
    public void initTcpServer(){
        rollbackInitData();
        startServer();
    }
    //因为数据库宕机或者服务器宕机要启动项目时进行数据回滚
    private void rollbackInitData(){
        rollbackTemplates.rollbackInitData();
    }

    @SneakyThrows
    private void startServer()  {
        Integer port = btProperties.getRollbackPort();
        if(tcpConnectThreadPool==null){
            synchronized (RollbackServer.class){
                if(tcpConnectThreadPool==null)
                    tcpConnectThreadPool=new ThreadPoolExecutor(3,13,1000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(50),new ThreadPoolExecutor.AbortPolicy());
            }
        }
        //启动TCPServer
        ServerSocket serverSocket = new ServerSocket(port);
        log.info("bt start listen port "+port);
        //先创建一个监听端口线程，为每到来一个链接就分配一个线程
        new Thread(()->{
            while(true){
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Socket clientSocket = socket;
                tcpConnectThreadPool.execute(()-> handleClientConnection(clientSocket));
            }
        }).start();
    }
    @SneakyThrows
    private void handleClientConnection(Socket clientSocket) {
        log.info(ServerConsts.BUILD_CONNECT);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            StringBuilder jsonRollback=new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                if(line.equals(ServerConsts.COMMON_TRANSACTION_EXECUTE)){
                    handleCommonExecute(JsonUtils.parseObject(jsonRollback.toString(),RollbackDto.class),out);
                    break;
                }
                if(line.equals(ServerConsts.ROLLBACK_TRANSACTION_EXECUTE)){
                    handlerRollbackExecute(JsonUtils.parseObject(jsonRollback.toString(),RollbackDto.class),out);
                    break;
                }
                jsonRollback.append(line);
            }
            out.println(ServerConsts.FINISH_CONNECT);
            //等待客户端关闭连接的时间，然后关闭连接
            Thread.sleep(3000);
            clientSocket.close();
            log.info(ServerConsts.FINISH_CONNECT);
        } catch (Exception e) {
            e.printStackTrace(); // 处理异常
        }
    }

    private void handleCommonExecute(RollbackDto rollbackDto,PrintWriter out){
        //...正常更新操作
        rollbackTemplates.commonExecute(rollbackDto);
    }

    private void handlerRollbackExecute(RollbackDto rollbackDto,PrintWriter out){
        //...回滚操作
        rollbackTemplates.rollback(rollbackDto);
    }
}
