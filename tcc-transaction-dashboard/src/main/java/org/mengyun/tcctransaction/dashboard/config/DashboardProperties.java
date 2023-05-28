package org.mengyun.tcctransaction.dashboard.config;

import org.mengyun.tcctransaction.dashboard.enums.ConnectionMode;
import org.mengyun.tcctransaction.properties.registry.RegistryProperties;

/**
 * @Author huabao.fang
 * @Date 2022/6/12 00:32
 */
public class DashboardProperties {

    private String userName = "admin";

    private String password = "123456";

    private ConnectionMode connectionMode;

    private RegistryProperties registry;

    public DashboardProperties() {
        RegistryProperties registryProperties = new RegistryProperties();
        registryProperties.getDirectRegistryProperties().setServerAddresses("http://localhost:12332");
        this.registry = registryProperties;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnectionMode getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(ConnectionMode connectionMode) {
        this.connectionMode = connectionMode;
    }

    public RegistryProperties getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryProperties registry) {
        this.registry = registry;
    }
}
