package org.mengyun.tcctransaction.discovery.registry;

import org.mengyun.tcctransaction.load.LoadUtils;

import java.util.*;

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

    public static List<RegistryService> getInstance(ServerRegistryConfig serverRegistryConfig) {
        if (serverRegistryConfig.getRegistryTypes() == null) {
            return Collections.emptyList();
        }
        List<RegistryService> registryServices = new ArrayList<>();
        Set<RegistryType> handled = new HashSet<>();
        for (RegistryType registryType : serverRegistryConfig.getRegistryTypes()) {
            if (!handled.contains(registryType)) {
                handled.add(registryType);
                registryServices.add(CANDIDATE_REGISTRY_PROVIDERS.get(registryType.name()).provide(serverRegistryConfig));
            }
        }
        return registryServices;
    }

    public static RegistryService getInstance(ClientRegistryConfig clientRegistryConfig) {
        return CANDIDATE_REGISTRY_PROVIDERS.get(clientRegistryConfig.getRegistryType().name()).provide(clientRegistryConfig);
    }

    public static RegistryProvider findRegistryProviderByName(String name) {
        return CANDIDATE_REGISTRY_PROVIDERS.get(name);
    }
}
