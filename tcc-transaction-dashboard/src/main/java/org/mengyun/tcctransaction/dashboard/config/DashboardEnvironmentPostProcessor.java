package org.mengyun.tcctransaction.dashboard.config;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.dashboard.constants.DashboardConstant;
import org.mengyun.tcctransaction.dashboard.enums.ConnectionMode;
import org.mengyun.tcctransaction.discovery.registry.RegistryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Optional;
import java.util.Properties;

/**
 * @Author huabao.fang
 * @Date 2022/6/21 19:23
 **/
public class DashboardEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String REGISTRY_DEFAULT_REGISTRY_TYPE = "direct";
    private static final String REGISTRY_DEFAULT_DIRECT_SERVER_ADDRESSES = "http://localhost:12332";
    private static final String REGISTRY_DEFAULT_ZOOKEEPER_CONNECT_STRING = "localhost:2181";
    private static final String REGISTRY_DEFAULT_NACOS_SERVER_ADDR = "localhost:8848";
    private Logger logger = LoggerFactory.getLogger(DashboardEnvironmentPostProcessor.class);
    private Properties tccDashboadProperties = new Properties() {{
        put("spring.tcc.storage.domain", DashboardConstant.APPLICATION_NAME);
        // dashbaord不需要执行recover task
        put("spring.tcc.recovery.recovery-enabled", "false");
        // server模式下 dashbaord需要管理集群的定时任务
        put("spring.tcc.recovery.quartz-clustered", "true");


        // freemarker
        put("spring.resources.chain.cache", "false");
        put("spring.resources.static-locations", "classpath:templates/");
        put("spring.freemarker.enabled", "true");
        put("spring.freemarker.cache", "false");
        put("spring.freemarker.charset", "UTF-8");
        put("spring.freemarker.suffix", ".html");
        put("spring.freemarker.check-template-location", "true");
        put("spring.freemarker.template-loader-path", "classpath:/templates/");

    }};

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        PropertySource<?> propertySource = selectApplicationConfigPropertySource(propertySources);
        if (propertySource != null) {// 读取application.yml文件内容来动态调整配置
            rebuildDashboardProperties(environment);
            rebuildDashboardRegistryProperties(environment);
            PropertiesPropertySource tccDashboardPropertySource = new PropertiesPropertySource("tccDashboadProperties", tccDashboadProperties);
            propertySources.addLast(tccDashboardPropertySource);
        }
    }

    private PropertySource selectApplicationConfigPropertySource(MutablePropertySources propertySources) {
        Optional<PropertySource<?>> propertySourceOptional = propertySources.stream().filter(propertySource -> propertySource.getName().contains("applicationConfig")).findFirst();
        return propertySourceOptional.isPresent() ? propertySourceOptional.get() : null;
    }

    private void rebuildDashboardRegistryProperties(ConfigurableEnvironment environment) {
        String connectionMode = environment.getProperty("spring.tcc.dashboard.connection-mode");
        if (StringUtils.isNotEmpty(connectionMode) && ConnectionMode.SERVER.name().equalsIgnoreCase(connectionMode)) {
            String registryType = environment.getProperty("spring.tcc.dashboard.registry.registry-type", REGISTRY_DEFAULT_REGISTRY_TYPE);
            if (RegistryType.direct.name().equals(registryType)) {
                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.enabled", "false");
                putIntoTccDashboadProperties("spring.cloud.zookeeper.enabled", "false");

                String serverAddresses = environment.getProperty("spring.tcc.dashboard.registry.direct.server-addresses", REGISTRY_DEFAULT_DIRECT_SERVER_ADDRESSES);
                String[] serverAddresseArr = serverAddresses.split(",");
                for (int i = 0; i < serverAddresseArr.length; i++) {
                    putIntoTccDashboadProperties("spring.cloud.discovery.client.simple.instances.tcc-transaction-server[" + i + "].uri",
                            serverAddresseArr[i]);
                }
                putIntoTccDashboadProperties("spring.cloud.loadbalancer.ribbon.enabled", "false");
            } else if (RegistryType.zookeeper.name().equals(registryType)) {
                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.enabled", "false");
                putIntoTccDashboadProperties("spring.cloud.zookeeper.enabled", "true");

                putIntoTccDashboadProperties("spring.cloud.zookeeper.connect-string", environment.getProperty("spring.tcc.dashboard.registry.zookeeper.connect-string", REGISTRY_DEFAULT_ZOOKEEPER_CONNECT_STRING));
            } else if (RegistryType.nacos.name().equals(registryType)) {
                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.enabled", "true");
                putIntoTccDashboadProperties("spring.cloud.zookeeper.enabled", "false");

                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.server-addr", environment.getProperty("spring.tcc.dashboard.registry.nacos.server-addr", REGISTRY_DEFAULT_NACOS_SERVER_ADDR));
                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.username", environment.getProperty("spring.tcc.dashboard.registry.nacos.username"));
                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.password", environment.getProperty("spring.tcc.dashboard.registry.nacos.password"));

            } else {
                throw new IllegalArgumentException("illegal registry type for " + registryType);
            }
        } else {
            tccDashboadProperties.put("spring.cloud.nacos.discovery.enabled", "false");
            tccDashboadProperties.put("spring.cloud.zookeeper.enabled", "false");
        }

    }


    private void rebuildDashboardProperties(ConfigurableEnvironment environment) {
        tccDashboadProperties.keySet().forEach(key -> {
            String value = environment.getProperty((String) key);
            if (!StringUtils.isBlank(value)) {
                tccDashboadProperties.put(key, value);
            }
        });
    }

    private void putIntoTccDashboadProperties(String key, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        this.tccDashboadProperties.put(key, value);

    }
}
