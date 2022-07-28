package org.mengyun.tcctransaction.discovery.registry.custom;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.discovery.registry.*;
import org.mengyun.tcctransaction.load.LoadInfo;

import java.util.stream.Stream;

/**
 * @author Nervose.Wu
 * @date 2022/5/17 14:42
 */
@LoadInfo(name = "custom")
public class CustomRegistryProvider implements RegistryProvider {

    @Override
    public RegistryService provide(RegistryConfig registryConfig) {
        String customRegistryName = registryConfig.getCustomRegistryName();
        if (StringUtils.isBlank(customRegistryName)) {
            throw new IllegalArgumentException("CustomRegistryName must not be blank");
        }

        if (Stream.of(RegistryType.values())
                .anyMatch(each -> each.name().equals(customRegistryName))) {
            throw new IllegalArgumentException(String.format("CustomRegistryName %s is not allowed", customRegistryName));
        }

        RegistryProvider actualRegistryProvider = RegistryFactory.findRegistryProviderByName(customRegistryName);

        if (actualRegistryProvider == null) {
            throw new IllegalArgumentException(String.format("Cant found RegistryProvider related to customRegistryName %s", customRegistryName));
        }
        return actualRegistryProvider.provide(registryConfig);
    }
}
