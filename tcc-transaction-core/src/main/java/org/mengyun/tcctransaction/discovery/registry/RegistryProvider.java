package org.mengyun.tcctransaction.discovery.registry;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 17:24
 */
public interface RegistryProvider {

    RegistryService provide(RegistryConfig registryConfig);
}
