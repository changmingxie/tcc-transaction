package org.mengyun.tcctransaction.dashboard;

import org.mengyun.ribbon.TccFeignClientConfig;
import org.mengyun.tcctransaction.dashboard.constants.DashboardConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by Lee on 2020/4/8 12:56.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients
@RibbonClients(value = {@RibbonClient(name = DashboardConstant.TCC_SERVER_GROUP, configuration = TccFeignClientConfig.class)})
public class TccTransactionDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(TccTransactionDashboardApplication.class, args);
    }
}
