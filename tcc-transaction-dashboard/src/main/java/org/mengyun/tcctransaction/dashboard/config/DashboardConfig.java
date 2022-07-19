package org.mengyun.tcctransaction.dashboard.config;

import org.mengyun.tcctransaction.properties.registry.RegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    // 不注入bean 仅用于application.yaml中registry相关参考格式定义
    @ConfigurationProperties("spring.tcc.dashboard.registry")
    public RegistryProperties dashboardRegistryProperties() {
        return new RegistryProperties();
    }
}
