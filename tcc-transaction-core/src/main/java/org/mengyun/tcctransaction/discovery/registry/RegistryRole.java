package org.mengyun.tcctransaction.discovery.registry;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 17:55
 */

public enum RegistryRole {
    BUSINESS,
    DASHBOARD;

    public static RegistryRole getType(String name) {
        for (RegistryRole registryRole : RegistryRole.values()) {
            if (registryRole.name().equalsIgnoreCase(name)) {
                return registryRole;
            }
        }
        throw new IllegalArgumentException("not support registry role: " + name);
    }
}
