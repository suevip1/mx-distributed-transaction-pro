package cn.distributed.transaction.framework.bt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.data.redis")
public class RedisProperties {
    private String host;

    private String port;

    private String database;

    private String password;
}
