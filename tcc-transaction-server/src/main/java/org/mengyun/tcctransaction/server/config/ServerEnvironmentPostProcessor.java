package org.mengyun.tcctransaction.server.config;

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
 * @Date 2022/7/18 13:23
 **/
public class ServerEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private Logger logger = LoggerFactory.getLogger(ServerEnvironmentPostProcessor.class);

    private Properties tccServerProperties = new Properties();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        PropertySource<?> propertySource = selectApplicationConfigPropertySource(propertySources);
        if (propertySource != null) {// 读取application.yml文件内容来动态调整配置
            rebuildRegistryProperties(environment);
            PropertiesPropertySource tccServerPropertySource = new PropertiesPropertySource("tccServerProperties", tccServerProperties);
            propertySources.addLast(tccServerPropertySource);
        }
    }

    private PropertySource selectApplicationConfigPropertySource(MutablePropertySources propertySources) {
        Optional<PropertySource<?>> propertySourceOptional = propertySources.stream().filter(propertySource -> propertySource.getName().contains("applicationConfig")).findFirst();
        return propertySourceOptional.isPresent() ? propertySourceOptional.get() : null;
    }

    private void rebuildRegistryProperties(ConfigurableEnvironment environment) {
        String registryType = environment.getProperty("spring.tcc.registry.registry-type");
        if (StringUtils.isBlank(registryType)) {
            registryType = environment.getProperty("spring.tcc.registry.registryType");
        }
        if (StringUtils.isBlank(registryType) || RegistryType.direct.name().equals(registryType)) {
            putIntoTccServerProperties("spring.cloud.nacos.discovery.enabled", "false");
            putIntoTccServerProperties("spring.cloud.zookeeper.enabled", "false");
        } else if (RegistryType.zookeeper.name().equals(registryType)) {
            putIntoTccServerProperties("spring.cloud.nacos.discovery.enabled", "false");
            putIntoTccServerProperties("spring.cloud.zookeeper.enabled", "true");

            putIntoTccServerProperties("spring.cloud.zookeeper.connect-string", environment.getProperty("spring.tcc.registry.zookeeper.connect-string"));
        } else if (RegistryType.nacos.name().equals(registryType)) {
            putIntoTccServerProperties("spring.cloud.nacos.discovery.enabled", "true");
            putIntoTccServerProperties("spring.cloud.zookeeper.enabled", "false");

            putIntoTccServerProperties("spring.cloud.nacos.discovery.server-addr", environment.getProperty("spring.tcc.registry.nacos.server-addr"));
            putIntoTccServerProperties("spring.cloud.nacos.discovery.username", environment.getProperty("spring.tcc.registry.nacos.username"));
            putIntoTccServerProperties("spring.cloud.nacos.discovery.password", environment.getProperty("spring.tcc.registry.nacos.password"));
        } else {
            logger.warn("unable to reset the registry config of spring clound for {}", registryType);
        }

    }

    private void putIntoTccServerProperties(String key, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        this.tccServerProperties.put(key, value);

    }

}
