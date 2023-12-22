package org.mengyun.tcctransaction.server.config;

import org.mengyun.tcctransaction.ServerConfig;
import org.mengyun.tcctransaction.TccServer;
import org.mengyun.tcctransaction.discovery.registry.ServerRegistryConfig;
import org.mengyun.tcctransaction.properties.RecoveryProperties;
import org.mengyun.tcctransaction.properties.registry.ServerRegistryProperties;
import org.mengyun.tcctransaction.properties.remoting.NettyServerProperties;
import org.mengyun.tcctransaction.properties.store.StoreProperties;
import org.mengyun.tcctransaction.recovery.RecoveryConfig;
import org.mengyun.tcctransaction.remoting.netty.NettyServerConfig;
import org.mengyun.tcctransaction.spring.factory.SpringBeanFactory;
import org.mengyun.tcctransaction.storage.StoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class AppConfig {

    @Bean
    @ConfigurationProperties("spring.tcc.remoting")
    public NettyServerProperties nettyServerProperties() {
        return new NettyServerProperties();
    }

    @Bean
    @ConfigurationProperties("spring.tcc.storage")
    public StoreProperties storeProperties() {
        return new StoreProperties();
    }

    @Bean
    @ConfigurationProperties("spring.tcc.registry")
    public ServerRegistryProperties registryProperties() {
        return new ServerRegistryProperties();
    }

    @Bean
    @ConfigurationProperties("spring.tcc.recovery")
    public RecoveryProperties recoveryProperties() {
        return new RecoveryProperties();
    }

    @Bean
    public ServerConfig serverConfig(@Autowired ServerRegistryConfig serverRegistryConfig,
                                     @Autowired StoreConfig storeConfig,
                                     @Autowired RecoveryConfig recoveryConfig,
                                     @Autowired NettyServerConfig nettyServerConfig) {
        return new ServerConfig(storeConfig, recoveryConfig, nettyServerConfig, serverRegistryConfig);
    }

    @Bean
    public TccServer tccServer(@Autowired ServerConfig serverConfig) {
        return new TccServer(serverConfig);
    }

    @Bean
    public SpringBeanFactory springBeanFactory() {
        return new SpringBeanFactory();
    }
}
