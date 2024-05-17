package cn.distributed.transaction.service0;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.distributed.transaction.service0.dao")
@EnableAspectJAutoProxy
public class Service0Application {
    public static void main(String[] args) {
        SpringApplication.run(Service0Application.class,args);
    }
}
