package org.mengyun.tcctransaction.sample.feign.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:17
 */
@SpringBootApplication(
        scanBasePackages = {"org.mengyun.tcctransaction.sample.feign.order", "org.mengyun.tcctransaction.sample"},
        exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ImportResource(locations = "classpath*:config/spring/local/appcontext-*.xml")
@EnableFeignClients
public class FeignOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignOrderApplication.class, args);
    }
}
