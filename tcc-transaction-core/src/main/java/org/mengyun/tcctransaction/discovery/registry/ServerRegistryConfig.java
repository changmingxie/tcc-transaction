package org.mengyun.tcctransaction.discovery.registry;

import java.util.List;

/**
 * @author Nervose.Wu
 * @date 2022/7/7 17:34
 */
public interface ServerRegistryConfig extends RegistryConfig {

    List<RegistryType> getRegistryTypes();

    String getRegistryAddress();

    int getRegistryPortForDashboard();

    String getRegistryAddressForDashboard();
}
