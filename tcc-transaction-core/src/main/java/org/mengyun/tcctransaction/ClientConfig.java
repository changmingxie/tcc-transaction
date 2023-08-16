package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.discovery.registry.ClientRegistryConfig;
import org.mengyun.tcctransaction.properties.registry.ClientRegistryProperties;
import org.mengyun.tcctransaction.properties.remoting.NettyClientProperties;
import org.mengyun.tcctransaction.recovery.RecoveryConfig;
import org.mengyun.tcctransaction.remoting.netty.NettyClientConfig;
import org.mengyun.tcctransaction.storage.StoreConfig;

public class ClientConfig extends AbstractConfig implements RecoveryConfig, NettyClientConfig, StoreConfig, ClientRegistryConfig {

    public static final ClientConfig DEFAULT = new ClientConfig();

    private NettyClientConfig nettyClientConfig = new NettyClientProperties();

    private ClientRegistryConfig clientRegistryConfig = new ClientRegistryProperties();

    private int asyncConfirmCancelThreadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private int asyncConfirmCancelThreadPoolQueueSize = 1024;

    public ClientConfig() {
    }

    public ClientConfig(StoreConfig storeConfig, RecoveryConfig recoveryConfig, NettyClientConfig nettyClientConfig, ClientRegistryConfig clientRegistryConfig) {
        super(storeConfig, recoveryConfig, nettyClientConfig, clientRegistryConfig);
        if (nettyClientConfig != null) {
            this.nettyClientConfig = nettyClientConfig;
        }
        if (clientRegistryConfig != null) {
            this.clientRegistryConfig = clientRegistryConfig;
        }
    }

    @Override
    public long getConnectTimeoutMillis() {
        return nettyClientConfig.getConnectTimeoutMillis();
    }

    @Override
    public int getChannelPoolMaxTotal() {
        return nettyClientConfig.getChannelPoolMaxTotal();
    }

    @Override
    public int getChannelPoolMaxIdlePerKey() {
        return nettyClientConfig.getChannelPoolMaxIdlePerKey();
    }

    @Override
    public int getChannelPoolMaxTotalPerKey() {
        return nettyClientConfig.getChannelPoolMaxTotalPerKey();
    }

    @Override
    public int getChannelPoolMinIdlePerKey() {
        return nettyClientConfig.getChannelPoolMinIdlePerKey();
    }

    @Override
    public long getChannelPoolMaxWaitMillis() {
        return nettyClientConfig.getChannelPoolMaxWaitMillis();
    }

    @Override
    public long getChannelPoolTimeBetweenEvictionRunsMillis() {
        return nettyClientConfig.getChannelPoolTimeBetweenEvictionRunsMillis();
    }

    @Override
    public long getChannelPoolSoftMinEvictableIdleTimeMillis() {
        return nettyClientConfig.getChannelPoolSoftMinEvictableIdleTimeMillis();
    }

    @Override
    public int getNumTestsPerEvictionRun() {
        return nettyClientConfig.getNumTestsPerEvictionRun();
    }


    @Override
    public int getChannelMaxIdleTimeSeconds() {
        return nettyClientConfig.getChannelMaxIdleTimeSeconds();
    }

    @Override
    public int getReconnectIntervalSeconds() {
        return nettyClientConfig.getReconnectIntervalSeconds();
    }

    @Override
    public String getLoadBalanceType() {
        return clientRegistryConfig.getLoadBalanceType();
    }

    public void setNettyClientConfig(NettyClientConfig nettyClientConfig) {
        this.nettyClientConfig = nettyClientConfig;
        setNettyConfig(nettyClientConfig);
    }

    public void setClientRegistryConfig(ClientRegistryConfig clientRegistryConfig) {
        this.clientRegistryConfig = clientRegistryConfig;
        setRegistryConfig(clientRegistryConfig);
    }

    public int getAsyncConfirmCancelThreadPoolSize() {
        return asyncConfirmCancelThreadPoolSize;
    }

    public void setAsyncConfirmCancelThreadPoolSize(int asyncConfirmCancelThreadPoolSize) {
        this.asyncConfirmCancelThreadPoolSize = asyncConfirmCancelThreadPoolSize;
    }

    public int getAsyncConfirmCancelThreadPoolQueueSize() {
        return asyncConfirmCancelThreadPoolQueueSize;
    }

    public void setAsyncConfirmCancelThreadPoolQueueSize(int asyncConfirmCancelThreadPoolQueueSize) {
        this.asyncConfirmCancelThreadPoolQueueSize = asyncConfirmCancelThreadPoolQueueSize;
    }
}
