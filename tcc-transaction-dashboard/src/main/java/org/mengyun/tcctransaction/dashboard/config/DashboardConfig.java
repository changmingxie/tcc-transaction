package org.mengyun.tcctransaction.dashboard.config;

import org.mengyun.tcctransaction.properties.registry.RegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @Author huabao.fang
 * @Date 2022/6/12 00:46
 **/
@EnableConfigurationProperties
public class DashboardConfig {

    @Bean
    @ConfigurationProperties("spring.tcc.dashboard")
    public DashboardProperties dashboardProperties() {
        return new DashboardProperties();
    }

    @Bean
    @ConfigurationProperties("spring.tcc.dashboard.registry")
    public RegistryProperties registryProperties() {
        return new RegistryProperties();
    }
}
