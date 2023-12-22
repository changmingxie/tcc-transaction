package org.mengyun.tcctransaction.discovery.registry.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import org.mengyun.tcctransaction.discovery.registry.AbstractRegistryService;
import org.mengyun.tcctransaction.discovery.registry.RegistryConfig;
import org.mengyun.tcctransaction.exception.RegistryException;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 16:33
 */
public class NacosRegistryServiceImpl extends AbstractRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(NacosRegistryServiceImpl.class.getSimpleName());

    private NamingService namingService;

    private NacosRegistryProperties properties;

    private InetSocketAddress address;
    private InetSocketAddress addressForDashboard;

    public NacosRegistryServiceImpl(RegistryConfig registryConfig) {
        setClusterName(registryConfig.getClusterName());
        this.properties = registryConfig.getNacosRegistryProperties();
    }

    @Override
    public void start() {
        Properties createProperties = new Properties();
        createProperties.put(PropertyKeyConst.NAMESPACE, properties.getNamespace());
        createProperties.put(PropertyKeyConst.SERVER_ADDR, properties.getServerAddr());
        createProperties.put(PropertyKeyConst.USERNAME, properties.getUsername());
        createProperties.put(PropertyKeyConst.PASSWORD, properties.getPassword());
        try {
            namingService = NacosFactory.createNamingService(createProperties);
        } catch (Exception e) {
            throw new RegistryException("Cant connect to the nacos", e);
        }
    }

    @Override
    protected void doRegister(InetSocketAddress address, InetSocketAddress addressForDashboard) throws Exception {
        this.address = address;
        this.addressForDashboard = addressForDashboard;
        String addressString = NetUtils.parseSocketAddress(address);
        boolean success = true;
        int index = addressString.indexOf(':');
        if (index == -1) {
            throw new IllegalArgumentException("invalid address：" + address.toString());
        }
        try {
            namingService.registerInstance(properties.getServiceName(), properties.getGroup(), addressString.substring(0, index), address.getPort(), getClusterName());
        } catch (NacosException e) {
            success = false;
            logger.error("Cant connect to the nacos", e);
        }
        addressString = NetUtils.parseSocketAddress(addressForDashboard);
        index = addressString.indexOf(':');
        if (index == -1) {
            throw new IllegalArgumentException("invalid addressForDashboard：" + addressForDashboard.toString());
        }
        try {
            namingService.registerInstance(properties.getServiceNameForDashboard(), properties.getGroup(), addressString.substring(0, index), addressForDashboard.getPort(), getClusterName());
        } catch (NacosException e) {
            success = false;
            logger.error("Cant connect to the nacos", e);
        }
        if (success) {
            logger.info("Registered with nacos. {},{}", address, addressForDashboard);
        }
    }


    @Override
    protected void doSubscribe(boolean addressForDashboard) throws Exception {
        String serviceName = addressForDashboard ? properties.getServiceNameForDashboard() : properties.getServiceName();
        setServerAddresses(namingService.selectInstances(serviceName, properties.getGroup(), Collections.singletonList(getClusterName()), true)
                .stream()
                .map(each -> each.getIp() + ":" + each.getPort())
                .collect(Collectors.toList()), addressForDashboard
        );
        namingService.subscribe(serviceName, properties.getGroup(), Collections.singletonList(getClusterName()), event -> {
            try {
                setServerAddresses(((NamingEvent) event).getInstances()
                        .stream()
                        .filter(each -> each.isEnabled() && each.isHealthy())
                        .map(each -> each.getIp() + ":" + each.getPort())
                        .collect(Collectors.toList()), addressForDashboard
                );
            } catch (Exception e) {
                logger.warn("Failed to update server addresses", e);
            }
        });
    }

    @Override
    public void close() {
        if (namingService == null) {
            return;
        }
        try {
            if (address != null) {
                String addressString = NetUtils.parseSocketAddress(address);
                int index = addressString.indexOf(':');
                namingService.deregisterInstance(properties.getServiceName(), properties.getGroup(), addressString.substring(0, index), address.getPort(), getClusterName());
            }
            if (addressForDashboard != null) {
                String addressString = NetUtils.parseSocketAddress(addressForDashboard);
                int index = addressString.indexOf(':');
                namingService.deregisterInstance(properties.getServiceNameForDashboard(), properties.getGroup(), addressString.substring(0, index), addressForDashboard.getPort(), getClusterName());
            }
        } catch (Exception e) {
            //ignore
        }
    }
}
