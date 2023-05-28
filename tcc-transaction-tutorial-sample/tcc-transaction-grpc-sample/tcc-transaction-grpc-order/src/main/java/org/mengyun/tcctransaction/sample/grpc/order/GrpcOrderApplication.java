package org.mengyun.tcctransaction.sample.grpc.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:17
 */
@SpringBootApplication(scanBasePackages = { "org.mengyun.tcctransaction.sample.grpc.order", "org.mengyun.tcctransaction.sample" }, exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class })
@ImportResource(locations = "classpath*:config/spring/local/appcontext-*.xml")
public class GrpcOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcOrderApplication.class, args);
    }
}
