package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.discovery.registry.ServerRegistryConfig;
import org.mengyun.tcctransaction.properties.CommonProperties;
import org.mengyun.tcctransaction.properties.registry.ServerRegistryProperties;
import org.mengyun.tcctransaction.properties.remoting.NettyServerProperties;
import org.mengyun.tcctransaction.recovery.RecoveryConfig;
import org.mengyun.tcctransaction.remoting.netty.NettyServerConfig;
import org.mengyun.tcctransaction.storage.StoreConfig;

public class ServerConfig extends AbstractConfig implements NettyServerConfig, RecoveryConfig, StoreConfig, ServerRegistryConfig {

    public final static ServerConfig DEFAULT = new ServerConfig();

    private NettyServerConfig nettyServerConfig = new NettyServerProperties();

    private ServerRegistryConfig serverRegistryConfig = new ServerRegistryProperties();

    public ServerConfig() {
    }

    public ServerConfig(CommonProperties commonProperties, StoreConfig storeConfig, RecoveryConfig recoveryConfig, NettyServerConfig nettyServerConfig, ServerRegistryConfig serverRegistryConfig) {
        super(commonProperties, storeConfig, recoveryConfig, nettyServerConfig, serverRegistryConfig);
        if (nettyServerConfig != null) {
            this.nettyServerConfig = nettyServerConfig;
        }
        if (serverRegistryConfig != null) {
            this.serverRegistryConfig = serverRegistryConfig;
        }
    }

    @Override
    public int getListenPort() {
        return nettyServerConfig.getListenPort();
    }

    @Override
    public int getChannelIdleTimeoutSeconds() {
        return nettyServerConfig.getChannelIdleTimeoutSeconds();
    }


    @Override
    public String getRegistryAddress() {
        return serverRegistryConfig.getRegistryAddress();
    }

    public void setNettyServerConfig(NettyServerConfig nettyServerConfig) {
        this.nettyServerConfig = nettyServerConfig;
        setNettyConfig(nettyServerConfig);
    }

    public void setServerRegistryConfig(ServerRegistryConfig serverRegistryConfig) {
        this.serverRegistryConfig = serverRegistryConfig;
    }
}
