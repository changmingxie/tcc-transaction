package org.mengyun.tcctransaction.discovery.registry.direct;

/**
 * @author Nervose.Wu
 * @date 2022/5/18 17:00
 */
public class DirectRegistryProperties {

    private String serverAddresses = "127.0.0.1:2332";

    private String serverAddressesForDashboard = "127.0.0.1:12332";

    public String getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(String serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public String getServerAddressesForDashboard() {
        return serverAddressesForDashboard;
    }

    public void setServerAddressesForDashboard(String serverAddressesForDashboard) {
        this.serverAddressesForDashboard = serverAddressesForDashboard;
    }
}
