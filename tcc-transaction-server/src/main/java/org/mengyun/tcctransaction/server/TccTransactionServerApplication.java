package org.mengyun.tcctransaction.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;

@SpringBootApplication(exclude = QuartzAutoConfiguration.class)
public class TccTransactionServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TccTransactionServerApplication.class, args);
    }
}
