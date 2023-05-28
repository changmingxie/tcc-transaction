package org.mengyun.tcctransaction.discovery.registry;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 17:55
 */
public enum RegistryType {

    zookeeper, nacos, direct, custom;

    public static RegistryType getType(String name) {
        for (RegistryType registryType : RegistryType.values()) {
            if (registryType.name().equalsIgnoreCase(name)) {
                return registryType;
            }
        }
        throw new IllegalArgumentException("not support registry type: " + name);
    }
}
