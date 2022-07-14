package org.mengyun.tcctransaction.sample.dubbo.redpacket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "classpath*:config/spring/local/appcontext-*.xml")
public class DubboRedpacketApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboRedpacketApplication.class, args);
    }
}
