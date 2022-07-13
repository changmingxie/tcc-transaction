package org.mengyun.tcctransaction.dashboard.config;

import org.mengyun.tcctransaction.dashboard.constants.DashboardConstant;
import org.mengyun.tcctransaction.dashboard.enums.DataFetchType;
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

import java.util.Iterator;
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

        //默认为本地模式，且存储类型为memory
//        put("spring.tcc.dashboard.data-fetch-type","local");
//        put("spring.tcc.storage.storage-type","memory");

        // data-fetch-type为tccserver:
        put("spring.tcc.dashboard.registry.registry-type", "direct");
        put("spring.tcc.dashboard.registry.direct.server-addresses", "http://localhost:9998");
        put("spring.tcc.dashboard.registry.nacos.server-addr", "localhost:8848");
        put("spring.tcc.dashboard.registry.zookeeper.connect-string", "localhost:2181");

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

    private PropertySource selectApplicationConfigPropertySource(MutablePropertySources propertySources){
        Optional<PropertySource<?>> propertySourceOptional = propertySources.stream().filter(propertySource -> propertySource.getName().contains("applicationConfig")).findFirst();
        return propertySourceOptional.isPresent()?propertySourceOptional.get():null;
    }

    private void rebuildDashboardRegistryProperties(ConfigurableEnvironment environment) {
        String dataFetchType = environment.getProperty("spring.tcc.dashboard.data-fetch-type");
        if (StringUtils.isNotEmpty(dataFetchType) && DataFetchType.TCCSERVER.name().equals(dataFetchType.toUpperCase())) {
            String registryType = tccDashboadProperties.getProperty("spring.tcc.dashboard.registry.registry-type");
            if (RegistryType.direct.name().equals(registryType)) {
                tccDashboadProperties.put("spring.cloud.nacos.discovery.enabled", "false");
                tccDashboadProperties.put("spring.cloud.zookeeper.enabled", "false");

                tccDashboadProperties.put("spring.cloud.discovery.client.simple.instances.tcc-transaction-server[0].uri",
                        tccDashboadProperties.getProperty("spring.tcc.dashboard.registry.direct.server-addresses"));
                tccDashboadProperties.put("spring.cloud.loadbalancer.ribbon.enabled", "false");
            } else if (RegistryType.zookeeper.name().equals(registryType)) {
                tccDashboadProperties.put("spring.cloud.nacos.discovery.enabled", "false");
                tccDashboadProperties.put("spring.cloud.zookeeper.enabled", "true");

                tccDashboadProperties.put("spring.cloud.zookeeper.connect-string",
                        tccDashboadProperties.getProperty("spring.tcc.dashboard.registry.zookeeper.connect-string"));
            } else if (RegistryType.nacos.name().equals(registryType)) {
                tccDashboadProperties.put("spring.cloud.nacos.discovery.enabled", "true");
                tccDashboadProperties.put("spring.cloud.zookeeper.enabled", "false");

                tccDashboadProperties.put("spring.cloud.nacos.discovery.server-addr",
                        tccDashboadProperties.getProperty("spring.tcc.dashboard.registry.nacos.server-addr"));

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
}
