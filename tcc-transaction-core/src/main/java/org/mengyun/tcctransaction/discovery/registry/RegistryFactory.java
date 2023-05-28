package org.mengyun.tcctransaction.discovery.registry;

import org.mengyun.tcctransaction.load.LoadUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 17:58
 */
public class RegistryFactory {

    private static final Map<String, RegistryProvider> CANDIDATE_REGISTRY_PROVIDERS = new HashMap<>();

    static {
        ServiceLoader.load(RegistryProvider.class).forEach(each -> CANDIDATE_REGISTRY_PROVIDERS.put(LoadUtils.getServiceName(each.getClass()), each));
    }

    private RegistryFactory() {
    }

    public static RegistryService getInstance(RegistryConfig registryConfig) {
        return CANDIDATE_REGISTRY_PROVIDERS.get(registryConfig.getRegistryType().name()).provide(registryConfig);
    }

    public static RegistryProvider findRegistryProviderByName(String name) {
        return CANDIDATE_REGISTRY_PROVIDERS.get(name);
    }
}
