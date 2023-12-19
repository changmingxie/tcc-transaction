package org.mengyun.tcctransaction.properties.registry;

import org.mengyun.tcctransaction.discovery.registry.RegistryType;
import org.mengyun.tcctransaction.discovery.registry.ServerRegistryConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nervose.Wu
 * @date 2022/7/7 17:38
 */
public class ServerRegistryProperties extends RegistryProperties implements ServerRegistryConfig {

    private List<RegistryType> registryTypes = new ArrayList<>();

    private String registryAddress;

    private int registryPortForDashboard = 12332;

    private String registryAddressForDashboard;

    @Override
    public List<RegistryType> getRegistryTypes() {
        return registryTypes;
    }

    public void setRegistryTypes(List<RegistryType> registryTypes) {
        this.registryTypes = registryTypes;
    }

    @Override
    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    @Override
    public int getRegistryPortForDashboard() {
        return registryPortForDashboard;
    }

    public void setRegistryPortForDashboard(int registryPortForDashboard) {
        this.registryPortForDashboard = registryPortForDashboard;
    }

    @Override
    public String getRegistryAddressForDashboard() {
        return registryAddressForDashboard;
    }

    public void setRegistryAddressForDashboard(String registryAddressForDashboard) {
        this.registryAddressForDashboard = registryAddressForDashboard;
    }
}
