package org.mengyun.tcctransaction.server.config;

import org.mengyun.tcctransaction.discovery.registry.RegistryType;
import org.mengyun.tcctransaction.utils.StringUtils;
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

    private Properties tccServerProperties = new Properties();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        PropertySource<?> propertySource = selectApplicationConfigPropertySource(propertySources);
        if (propertySource != null) {// 读取application.yml文件内容来动态调整配置
            rebuildDashboardRegistryProperties(environment);
            PropertiesPropertySource tccServerPropertySource = new PropertiesPropertySource("tccServerProperties", tccServerProperties);
            propertySources.addLast(tccServerPropertySource);
        }
    }

    private PropertySource selectApplicationConfigPropertySource(MutablePropertySources propertySources) {
        Optional<PropertySource<?>> propertySourceOptional = propertySources.stream().filter(propertySource -> propertySource.getName().contains("applicationConfig")).findFirst();
        return propertySourceOptional.isPresent() ? propertySourceOptional.get() : null;
    }

    private void rebuildDashboardRegistryProperties(ConfigurableEnvironment environment) {
        String registryType = environment.getProperty("spring.tcc.registry.registry-type");
        if(StringUtils.isBlank(registryType)){
            registryType = environment.getProperty("spring.tcc.registry.registryType");
        }
        if (StringUtils.isBlank(registryType)||RegistryType.direct.name().equals(registryType)) {
            tccServerProperties.put("spring.cloud.nacos.discovery.enabled", "false");
            tccServerProperties.put("spring.cloud.zookeeper.enabled", "false");
        } else if (RegistryType.zookeeper.name().equals(registryType)) {
            tccServerProperties.put("spring.cloud.nacos.discovery.enabled", "false");
            tccServerProperties.put("spring.cloud.zookeeper.enabled", "true");

            tccServerProperties.put("spring.cloud.zookeeper.connect-string",
                    environment.getProperty("spring.tcc.registry.zookeeper.connect-string"));
        } else if (RegistryType.nacos.name().equals(registryType)) {
            tccServerProperties.put("spring.cloud.nacos.discovery.enabled", "true");
            tccServerProperties.put("spring.cloud.zookeeper.enabled", "false");

            tccServerProperties.put("spring.cloud.nacos.discovery.server-addr",
                    environment.getProperty("spring.tcc.registry.nacos.server-addr"));
        } else {
            throw new IllegalArgumentException("illegal registry type for " + registryType);
        }

    }

}
