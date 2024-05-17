package cn.distributed.transaction.framework.bt.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("bt")
public class BTProperties {
    private String[] paths;

    private Integer rollbackPort;

    private String host;
}
