package org.mengyun.tcctransaction.discovery.registry;

/**
 * @author Nervose.Wu
 * @date 2022/7/7 17:34
 */
public interface ClientRegistryConfig extends RegistryConfig {

    RegistryType getRegistryType();

    RegistryRole getRegistryRole();

    String getLoadBalanceType();
}
