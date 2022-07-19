package org.mengyun.tcctransaction.dashboard.config;

import org.mengyun.tcctransaction.dashboard.constants.DashboardConstant;
import org.mengyun.tcctransaction.dashboard.enums.ConnectionMode;
import org.mengyun.tcctransaction.discovery.registry.RegistryType;
import org.mengyun.tcctransaction.utils.StringUtils;
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

    private Logger logger = LoggerFactory.getLogger(DashboardEnvironmentPostProcessor.class);

    private Properties tccDashboadProperties = new Properties() {{
        // dashbaord不需要执行recover task
        put("spring.tcc.recovery.recovery-enabled", "false");
        // tccserver模式下 dashbaord需要管理集群的定时任务
        put("spring.tcc.recovery.quartz-clustered", "true");
        put("spring.tcc.storage.domain", DashboardConstant.APPLICATION_NAME);

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
        if (StringUtils.isNotEmpty(connectionMode) && ConnectionMode.SERVER.name().equals(connectionMode.toUpperCase())) {
            String registryType = environment.getProperty("spring.tcc.dashboard.registry.registry-type");
            if (RegistryType.direct.name().equals(registryType)) {
                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.enabled", "false");
                putIntoTccDashboadProperties("spring.cloud.zookeeper.enabled", "false");

                putIntoTccDashboadProperties("spring.cloud.discovery.client.simple.instances.tcc-transaction-server[0].uri",
                        environment.getProperty("spring.tcc.dashboard.registry.direct.server-addresses"));
                putIntoTccDashboadProperties("spring.cloud.loadbalancer.ribbon.enabled", "false");
            } else if (RegistryType.zookeeper.name().equals(registryType)) {
                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.enabled", "false");
                putIntoTccDashboadProperties("spring.cloud.zookeeper.enabled", "true");

                putIntoTccDashboadProperties("spring.cloud.zookeeper.connect-string", environment.getProperty("spring.tcc.dashboard.registry.zookeeper.connect-string"));
            } else if (RegistryType.nacos.name().equals(registryType)) {
                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.enabled", "true");
                putIntoTccDashboadProperties("spring.cloud.zookeeper.enabled", "false");

                putIntoTccDashboadProperties("spring.cloud.nacos.discovery.server-addr", environment.getProperty("spring.tcc.dashboard.registry.nacos.server-addr"));
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
