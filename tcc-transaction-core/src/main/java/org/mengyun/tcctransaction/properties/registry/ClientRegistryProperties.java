package org.mengyun.tcctransaction.properties.registry;

import org.mengyun.tcctransaction.discovery.loadbalance.LoadBalanceType;
import org.mengyun.tcctransaction.discovery.registry.ClientRegistryConfig;
import org.mengyun.tcctransaction.discovery.registry.RegistryRole;
import org.mengyun.tcctransaction.discovery.registry.RegistryType;

/**
 * @author Nervose.Wu
 * @date 2022/7/7 17:38
 */
public class ClientRegistryProperties extends RegistryProperties implements ClientRegistryConfig {

    private RegistryType registryType = RegistryType.direct;

    private RegistryRole registryRole = RegistryRole.BUSINESS;

    private String loadBalanceType = LoadBalanceType.RoundRobin.name();

    @Override
    public RegistryType getRegistryType() {
        return registryType;
    }

    public void setRegistryType(RegistryType registryType) {
        this.registryType = registryType;
    }

    @Override
    public String getLoadBalanceType() {
        return loadBalanceType;
    }

    public void setLoadBalanceType(String loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }

    @Override
    public RegistryRole getRegistryRole() {
        return registryRole;
    }

    public void setRegistryRole(RegistryRole registryRole) {
        this.registryRole = registryRole;
    }
}
