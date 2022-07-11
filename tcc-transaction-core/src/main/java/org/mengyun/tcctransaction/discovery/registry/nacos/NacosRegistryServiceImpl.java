package org.mengyun.tcctransaction.discovery.registry.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
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
    protected void doRegister(InetSocketAddress address) throws Exception {
        String addressString = NetUtils.parseSocketAddress(address);
        int index = addressString.indexOf(":");
        if (index == -1) {
            throw new IllegalArgumentException("invalid address：" + address.toString());
        }
        namingService.registerInstance(properties.getServiceName(), properties.getGroup(), addressString.substring(0, index), address.getPort(), getClusterName());

        logger.info("Registered with nacos");
    }


    @Override
    protected void doSubscribe() throws Exception {
        setServerAddresses(namingService.selectInstances(properties.getServiceName(), properties.getGroup(), Collections.singletonList(getClusterName()), true)
                .stream()
                .map(each -> new InetSocketAddress(each.getIp(), each.getPort()))
                .collect(Collectors.toList())
        );
        namingService.subscribe(properties.getServiceName(), properties.getGroup(), Collections.singletonList(getClusterName()), event -> {
            try {
                setServerAddresses(((NamingEvent) event).getInstances()
                        .stream()
                        .filter(each -> each.isEnabled() && each.isHealthy())
                        .map(each -> new InetSocketAddress(each.getIp(), each.getPort()))
                        .collect(Collectors.toList())
                );
            } catch (Exception e) {
                logger.warn("Failed to update server addresses", e);
            }
        });
    }
}
