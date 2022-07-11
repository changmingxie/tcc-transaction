package org.mengyun.tcctransaction.sample.feign.capital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 14:10
 */
@SpringBootApplication(
        scanBasePackages = {"org.mengyun.tcctransaction.sample.feign.capital", "org.mengyun.tcctransaction.sample"},
        exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ImportResource(locations = "classpath*:config/spring/local/appcontext-*.xml")
public class FeignCapitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignCapitalApplication.class, args);
    }
}
