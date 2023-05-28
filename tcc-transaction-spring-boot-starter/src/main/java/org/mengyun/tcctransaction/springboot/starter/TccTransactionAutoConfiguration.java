package org.mengyun.tcctransaction.springboot.starter;

import net.devh.springboot.autoconfigure.grpc.client.GlobalClientInterceptorConfigurerAdapter;
import net.devh.springboot.autoconfigure.grpc.client.GlobalClientInterceptorRegistry;
import net.devh.springboot.autoconfigure.grpc.server.GlobalServerInterceptorConfigurerAdapter;
import net.devh.springboot.autoconfigure.grpc.server.GlobalServerInterceptorRegistry;
import org.mengyun.tcctransaction.ClientConfig;
import org.mengyun.tcctransaction.grpc.interceptor.TransactionContextClientInterceptor;
import org.mengyun.tcctransaction.grpc.interceptor.TransactionContextServerInterceptor;
import org.mengyun.tcctransaction.properties.RecoveryProperties;
import org.mengyun.tcctransaction.properties.registry.ClientRegistryProperties;
import org.mengyun.tcctransaction.properties.remoting.NettyClientProperties;
import org.mengyun.tcctransaction.properties.store.StoreProperties;
import org.mengyun.tcctransaction.recovery.RecoveryConfig;
import org.mengyun.tcctransaction.remoting.netty.NettyClientConfig;
import org.mengyun.tcctransaction.spring.annotation.EnableTccTransaction;
import org.mengyun.tcctransaction.storage.StoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nervose.Wu
 * @date 2022/5/26 11:48
 */
@EnableTccTransaction
@EnableConfigurationProperties
public class TccTransactionAutoConfiguration {

    @Bean
    @ConfigurationProperties("spring.tcc.remoting")
    public NettyClientProperties nettyClientProperties() {
        return new NettyClientProperties();
    }

    @Bean
    @ConfigurationProperties("spring.tcc.storage")
    public StoreProperties storeProperties() {
        return new StoreProperties();
    }

    @Bean
    @ConfigurationProperties("spring.tcc.registry")
    public ClientRegistryProperties registryProperties() {
        return new ClientRegistryProperties();
    }

    @Bean
    @ConfigurationProperties("spring.tcc.recovery")
    public RecoveryProperties recoveryProperties() {
        return new RecoveryProperties();
    }

    @Bean
    public ClientConfig clientConfig(@Autowired ClientRegistryProperties clientRegistryProperties, @Autowired StoreConfig storeConfig, @Autowired RecoveryConfig recoveryConfig, @Autowired NettyClientConfig nettyClientConfig) {
        return new ClientConfig(storeConfig, recoveryConfig, nettyClientConfig, clientRegistryProperties);
    }

    @Configuration
    @ConditionalOnClass({ TransactionContextClientInterceptor.class, GlobalClientInterceptorConfigurerAdapter.class })
    static class GrpcClientConfiguration {

        @Bean
        GlobalClientInterceptorConfigurerAdapter globalClientInterceptorConfigurerAdapter() {
            return new GlobalClientInterceptorConfigurerAdapter() {

                @Override
                public void addClientInterceptors(GlobalClientInterceptorRegistry registry) {
                    registry.addClientInterceptors(new TransactionContextClientInterceptor());
                }
            };
        }
    }

    @Configuration
    @ConditionalOnClass({ TransactionContextServerInterceptor.class, GlobalServerInterceptorConfigurerAdapter.class })
    static class GrpcServerConfiguration {

        @Bean
        GlobalServerInterceptorConfigurerAdapter globalServerInterceptorConfigurerAdapter() {
            return new GlobalServerInterceptorConfigurerAdapter() {

                @Override
                public void addServerInterceptors(GlobalServerInterceptorRegistry registry) {
                    registry.addServerInterceptors(new TransactionContextServerInterceptor());
                }
            };
        }
    }
}
