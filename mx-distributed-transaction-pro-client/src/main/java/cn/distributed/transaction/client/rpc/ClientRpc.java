package cn.distributed.transaction.client.rpc;


import cn.distributed.transaction.service0.api.Service0BookApi;
import cn.distributed.transaction.service1.api.Service1BookApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

@Component
@EnableFeignClients(clients = {Service0BookApi.class, Service1BookApi.class})
public class ClientRpc {
}
