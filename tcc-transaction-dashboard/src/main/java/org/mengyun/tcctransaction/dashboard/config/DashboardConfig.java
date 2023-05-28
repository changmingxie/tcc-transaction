package org.mengyun.tcctransaction.dashboard.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.CuratorFrameworkCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/6/12 00:46
 */
@EnableConfigurationProperties
@Configuration
public class DashboardConfig {

    @Bean
    @ConfigurationProperties("spring.tcc.dashboard")
    public DashboardProperties dashboardProperties() {
        return new DashboardProperties();
    }

    @Bean
    public CuratorFrameworkCustomizer curatorFrameworkCustomizer(DashboardProperties dashboardProperties) {
        return builder -> {
            if (StringUtils.isNotEmpty(dashboardProperties.getRegistry().getZookeeperRegistryProperties().getDigest())) {
                builder.authorization("digest", dashboardProperties.getRegistry().getZookeeperRegistryProperties().getDigest().getBytes()).aclProvider(new ACLProvider() {

                    @Override
                    public List<ACL> getDefaultAcl() {
                        return ZooDefs.Ids.CREATOR_ALL_ACL;
                    }

                    @Override
                    public List<ACL> getAclForPath(String path) {
                        return ZooDefs.Ids.CREATOR_ALL_ACL;
                    }
                });
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
