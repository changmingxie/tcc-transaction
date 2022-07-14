package org.mengyun.tcctransaction.sample.dubbo.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "classpath*:config/spring/local/appcontext-*.xml")
public class DubboOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboOrderApplication.class, args);
    }
}
