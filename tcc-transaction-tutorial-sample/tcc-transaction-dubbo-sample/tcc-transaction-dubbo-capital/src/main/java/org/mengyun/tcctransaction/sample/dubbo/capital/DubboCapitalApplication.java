package org.mengyun.tcctransaction.sample.dubbo.capital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "classpath*:config/spring/local/appcontext-*.xml")
public class DubboCapitalApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboCapitalApplication.class, args);
    }
}
