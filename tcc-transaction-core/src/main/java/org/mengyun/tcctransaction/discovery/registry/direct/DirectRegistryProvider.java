package org.mengyun.tcctransaction.discovery.registry.direct;

import org.mengyun.tcctransaction.discovery.registry.RegistryConfig;
import org.mengyun.tcctransaction.discovery.registry.RegistryProvider;
import org.mengyun.tcctransaction.discovery.registry.RegistryService;
import org.mengyun.tcctransaction.load.LoadInfo;

/**
 * @author Nervose.Wu
 * @date 2022/5/18 17:11
 */
@LoadInfo(name = "direct")
public class DirectRegistryProvider implements RegistryProvider {

    @Override
    public RegistryService provide(RegistryConfig registryConfig) {
        return new DirectRegistryServiceImpl(registryConfig);
    }
}
