package org.mengyun.tcctransaction.server;

import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.mengyun.tcctransaction.ha.zookeeper.CuratorFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Lee on 2020/9/28 16:21.
 * aggregate-framework
 */
@ConfigurationProperties("zookeeper")
@Data
public class CuratorBuilder {

    private int connectionTimout = 15 * 1000;
    private int sessionTimeout = 60 * 1000;
    private int baseSleepMs = 500;
    private int maxRetries = 12;
    private String connectString;
    private String digest = null;


    public CuratorFramework make() throws Exception {

        return CuratorFactory.make(connectString, digest, sessionTimeout, connectionTimout, baseSleepMs, maxRetries);
    }
}
