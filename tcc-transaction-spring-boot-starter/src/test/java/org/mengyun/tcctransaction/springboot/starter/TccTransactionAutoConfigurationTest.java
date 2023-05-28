package org.mengyun.tcctransaction.springboot.starter;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mengyun.tcctransaction.TccClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nervose.Wu
 * @date 2022/5/26 15:25
 */
public class TccTransactionAutoConfigurationTest {

    private ApplicationContextRunner runner = new ApplicationContextRunner().withInitializer(new ConfigFileApplicationContextInitializer()).withUserConfiguration(TestConfig.class);

    @Test
    public void tccClient() {
        runner.withPropertyValues("spring.application.name=tccClient", "spring.config.name=application-client").run(context -> Assertions.assertThat(context).hasSingleBean(TccClient.class));
    }

    @Configuration
    @EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, RedisAutoConfiguration.class })
    static class TestConfig {
    }
}
