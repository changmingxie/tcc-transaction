package org.mengyun.tcctransaction.properties.registry;

import org.mengyun.tcctransaction.discovery.registry.ServerRegistryConfig;

/**
 * @author Nervose.Wu
 * @date 2022/7/7 17:38
 */
public class ServerRegistryProperties extends RegistryProperties implements ServerRegistryConfig {

    private String registryAddress;

    @Override
    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }
}
