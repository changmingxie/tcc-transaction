package org.mengyun.tcctransaction.properties.registry;

import org.mengyun.tcctransaction.discovery.loadbalance.LoadBalanceType;
import org.mengyun.tcctransaction.discovery.registry.ClientRegistryConfig;

/**
 * @author Nervose.Wu
 * @date 2022/7/7 17:38
 */
public class ClientRegistryProperties extends RegistryProperties implements ClientRegistryConfig {

    private String loadBalanceType = LoadBalanceType.RoundRobin.name();

    @Override
    public String getLoadBalanceType() {
        return loadBalanceType;
    }

    public void setLoadBalanceType(String loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }
}
