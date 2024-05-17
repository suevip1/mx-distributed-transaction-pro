package cn.distributed.transaction.framework.bt;

import cn.distributed.transaction.framework.bt.config.*;
import cn.distributed.transaction.framework.bt.consts.ServerConsts;
import cn.distributed.transaction.framework.bt.properties.BTProperties;
import cn.distributed.transaction.framework.bt.server.RollbackServer;
import cn.distributed.transaction.framework.bt.server.RollbackTemplates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BTProperties.class)
@ConditionalOnProperty(ServerConsts.ENABLE_BT)
public class BTAutoConfiguration {
    @Bean
    public BTHttpServletRequestInterceptor btHttpServletRequestInterceptor(){
        return new BTHttpServletRequestInterceptor();
    }
    @Bean
    public BTTableMetaConfig btTableMetaConfig(){
        return new BTTableMetaConfig();
    }
    @Bean
    public InterceptorConfig interceptorConfig(){
        return new InterceptorConfig();
    }
    @Bean
    public RedissonTemplate redissonTemplate(){
        return new RedissonTemplate();
    }
    @Bean
    public MybatisBTInterceptor mybatisBTInterceptor(){
        return new MybatisBTInterceptor();
    }

    @Bean
    public RollbackServer rollbackServer(){
        return new RollbackServer();
    }
    @Bean
    public RollbackTemplates rollbackTemplates(){
        return new RollbackTemplates();
    }
}
