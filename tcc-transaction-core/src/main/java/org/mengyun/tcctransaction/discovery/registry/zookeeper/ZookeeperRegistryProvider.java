package org.mengyun.tcctransaction.discovery.registry.zookeeper;

import org.mengyun.tcctransaction.discovery.registry.RegistryConfig;
import org.mengyun.tcctransaction.discovery.registry.RegistryProvider;
import org.mengyun.tcctransaction.discovery.registry.RegistryService;
import org.mengyun.tcctransaction.load.LoadInfo;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 17:27
 */
@LoadInfo(name = "zookeeper")
public class ZookeeperRegistryProvider implements RegistryProvider {

    @Override
    public RegistryService provide(RegistryConfig registryConfig) {
        return new ZookeeperRegistryServiceImpl(registryConfig);
    }
}
