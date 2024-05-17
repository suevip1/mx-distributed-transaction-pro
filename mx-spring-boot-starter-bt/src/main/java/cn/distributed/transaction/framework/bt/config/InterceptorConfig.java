package cn.distributed.transaction.framework.bt.config;

import cn.distributed.transaction.framework.bt.consts.ServerConsts;
import cn.distributed.transaction.framework.bt.properties.BTProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties(BTProperties.class)
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private BTProperties btProperties;

    @Autowired
    private BTHttpServletRequestInterceptor btHttpServletRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 这里添加的路径不包含项目的contextPath哦
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(btHttpServletRequestInterceptor);
        for(String path:btProperties.getPaths()){
            interceptorRegistration=interceptorRegistration.addPathPatterns(path);
        }
    }
}
