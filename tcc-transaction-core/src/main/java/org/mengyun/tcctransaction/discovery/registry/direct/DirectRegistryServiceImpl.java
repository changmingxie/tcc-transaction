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
    protected void doRegister(InetSocketAddress address) throws Exception {
        //do nothing
    }

    @Override
    protected void doSubscribe() throws Exception {
        String serverAddresses = properties.getServerAddresses();
        if (StringUtils.isBlank(serverAddresses)) {
            throw new IllegalArgumentException("ServerAddresses cant be blank");
        }
        try {
            setServerAddresses(Arrays.stream(serverAddresses.split(",")).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse serverAddresses:" + serverAddresses);
        }
    }
}
