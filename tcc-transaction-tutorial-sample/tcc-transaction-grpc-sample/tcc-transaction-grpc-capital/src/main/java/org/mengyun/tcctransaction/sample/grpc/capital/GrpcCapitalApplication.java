package org.mengyun.tcctransaction.sample.grpc.capital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 14:10
 */
@SpringBootApplication(
        scanBasePackages = {"org.mengyun.tcctransaction.sample.grpc.capital", "org.mengyun.tcctransaction.sample"},
        exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ImportResource(locations = "classpath*:config/spring/local/appcontext-*.xml")
@EnableAspectJAutoProxy(exposeProxy = true)
public class GrpcCapitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcCapitalApplication.class, args);
    }

}
