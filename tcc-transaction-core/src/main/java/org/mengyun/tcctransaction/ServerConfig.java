package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.discovery.registry.RegistryType;
import org.mengyun.tcctransaction.discovery.registry.ServerRegistryConfig;
import org.mengyun.tcctransaction.properties.registry.ServerRegistryProperties;
import org.mengyun.tcctransaction.properties.remoting.NettyServerProperties;
import org.mengyun.tcctransaction.recovery.RecoveryConfig;
import org.mengyun.tcctransaction.remoting.netty.NettyServerConfig;
import org.mengyun.tcctransaction.storage.StoreConfig;

import java.util.List;

public class ServerConfig extends AbstractConfig implements NettyServerConfig, RecoveryConfig, StoreConfig, ServerRegistryConfig {

    public static final ServerConfig DEFAULT = new ServerConfig();

    private NettyServerConfig nettyServerConfig = new NettyServerProperties();

    private ServerRegistryConfig serverRegistryConfig = new ServerRegistryProperties();

    public ServerConfig() {
    }

    public ServerConfig(StoreConfig storeConfig, RecoveryConfig recoveryConfig, NettyServerConfig nettyServerConfig, ServerRegistryConfig serverRegistryConfig) {
        super(storeConfig, recoveryConfig, nettyServerConfig, serverRegistryConfig);
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
    public int getFlowMonitorPrintIntervalMinutes() {
        return nettyServerConfig.getFlowMonitorPrintIntervalMinutes();
    }


    @Override
    public List<RegistryType> getRegistryTypes() {
        return serverRegistryConfig.getRegistryTypes();
    }

    @Override
    public String getRegistryAddress() {
        return serverRegistryConfig.getRegistryAddress();
    }

    @Override
    public int getRegistryPortForDashboard() {
        return serverRegistryConfig.getRegistryPortForDashboard();
    }

    @Override
    public String getRegistryAddressForDashboard() {
        return serverRegistryConfig.getRegistryAddressForDashboard();
    }

    public void setNettyServerConfig(NettyServerConfig nettyServerConfig) {
        this.nettyServerConfig = nettyServerConfig;
        setNettyConfig(nettyServerConfig);
    }

    public void setServerRegistryConfig(ServerRegistryConfig serverRegistryConfig) {
        this.serverRegistryConfig = serverRegistryConfig;
        setRegistryConfig(serverRegistryConfig);
    }
}
