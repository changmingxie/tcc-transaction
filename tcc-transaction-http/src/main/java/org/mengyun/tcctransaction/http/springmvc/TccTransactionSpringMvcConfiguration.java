package org.mengyun.tcctransaction.http.springmvc;

import org.mengyun.tcctransaction.http.springmvc.interceptor.RequesterInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TccTransactionSpringMvcConfiguration implements WebMvcConfigurer {
    @Bean
    public RequesterInterceptor requesterInterceptor() {
        return new RequesterInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requesterInterceptor()).addPathPatterns("/**");
    }
}
