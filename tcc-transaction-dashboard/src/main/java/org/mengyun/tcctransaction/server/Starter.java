package org.mengyun.tcctransaction.server;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by Lee on 2020/4/8 12:56.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, QuartzAutoConfiguration.class})
@EnableConfigurationProperties(CuratorBuilder.class)
public class Starter {


    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }

    @Bean(destroyMethod = "close")
    public CuratorFramework curator(CuratorBuilder builder) throws Exception {
        return builder.make();
    }
}
