package org.mengyun.ribbon;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.mengyun.tcctransaction.TccClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nervose.Wu
 * @date 2023/2/8 16:48
 */
@Configuration
public class TccFeignClientConfig {

    @Autowired
    private TccClient tccClient;

    @Bean
    ServerList<Server> tccServerList(TccClient tccClient){
        return new TccServerList<>(tccClient.getRegistryService());
    }
}