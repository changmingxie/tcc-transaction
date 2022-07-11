package org.mengyun.tcctransaction.http.feign;

import org.mengyun.tcctransaction.http.feign.interceptor.FeignInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TccTransactionFeignConfiguration {

    @Bean
    public FeignInterceptor feignInterceptor() {
        return new FeignInterceptor();
    }
}
