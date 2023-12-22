package org.mengyun.tcctransaction.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Author huabao.fang
 * @Date 2022/6/12 00:46
 **/
@EnableConfigurationProperties
@Configuration
public class DashboardConfig {

    @Bean
    @ConfigurationProperties("spring.tcc.dashboard")
    public DashboardProperties dashboardProperties() {
        return new DashboardProperties();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
