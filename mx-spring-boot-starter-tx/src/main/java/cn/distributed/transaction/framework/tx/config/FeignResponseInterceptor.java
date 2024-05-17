package cn.distributed.transaction.framework.tx.config;

import cn.distributed.transaction.common.exception.BizException;
import cn.distributed.transaction.common.exception.enums.BizStatusEnum;
import cn.distributed.transaction.framework.tx.consts.TXConsts;
import cn.distributed.transaction.framework.tx.thread.TransactionThreadLocal;
import feign.Client;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FeignResponseInterceptor {
    @Bean
    public CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory(SpringClientFactory clientFactory) {
        return new CachingSpringLoadBalancerFactory(clientFactory);
    }

    @Bean
    public Client feignClient( SpringClientFactory clientFactory,CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new FeignOkHttpClientResponseInterceptor())
                .build();
        Client client = new feign.okhttp.OkHttpClient(okHttpClient);
        return new LoadBalancerFeignClient(client, cachingSpringLoadBalancerFactory, clientFactory);
    }


    public static class FeignOkHttpClientResponseInterceptor implements Interceptor{

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            Response respons = chain.proceed(request);
            if(!TransactionThreadLocal.isTransaction())
                return respons;
            String success = respons.headers().get(TXConsts.TX_RESPONSE_HEADER_SUCCESS);
            if(success==null||!success.equals("true"))
                throw new BizException(BizStatusEnum.TRANSACTION_ERROR_TX_ROLLBACK);
            handlerResponse(respons);
            return respons;
        }

        private static void handlerResponse(Response respons) {
            String locksList = respons.headers().get(TXConsts.TX_RESPONSE_HEADER_LOCKS);
            String port = respons.headers().get(TXConsts.TX_RESPONSE_HEADER_PORT);
            String host =respons.headers().get(TXConsts.TX_RESPONSE_HEADER_HOST);
            TransactionThreadLocal.updateLastBTLocal(locksList,host,Integer.parseInt(port));
        }
    }
}
