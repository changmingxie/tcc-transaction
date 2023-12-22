package org.mengyun.tcctransaction.discovery.registry.direct;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.discovery.registry.AbstractRegistryService;
import org.mengyun.tcctransaction.discovery.registry.RegistryConfig;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Nervose.Wu
 * @date 2022/5/18 17:11
 */
public class DirectRegistryServiceImpl extends AbstractRegistryService {

    private DirectRegistryProperties properties;

    public DirectRegistryServiceImpl(RegistryConfig registryConfig) {
        this.properties = registryConfig.getDirectRegistryProperties();
    }

    @Override
    protected void doRegister(InetSocketAddress address, InetSocketAddress addressForDashboard) {
        //do nothing
    }

    @Override
    protected void doSubscribe(boolean addressForDashboard) {
        String addresses = addressForDashboard ? properties.getServerAddressesForDashboard() : properties.getServerAddresses();
        if (StringUtils.isBlank(addresses)) {
            throw new IllegalArgumentException("ServerAddresses/ServerAddressesForDashboard cant be blank");
        }
        try {
            setServerAddresses(Arrays
                    .stream(addresses.split(","))
                    .collect(Collectors.toList()), addressForDashboard);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse serverAddresses/serverAddressesForDashboard:" + addresses);
        }
    }
}
