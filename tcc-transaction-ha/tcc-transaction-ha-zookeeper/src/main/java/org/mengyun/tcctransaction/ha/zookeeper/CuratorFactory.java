package org.mengyun.tcctransaction.ha.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lee on 2020/9/14 14:08.
 * aggregate-framework
 */
public class CuratorFactory {


    public static CuratorFramework make(String connectString,
                                        String digest,
                                        int sessionTimeout,
                                        int connectTimeout,
                                        int baseSleepTime,
                                        int maxRetries) throws Exception {


        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectTimeout)
                .retryPolicy(new ExponentialBackoffRetry(baseSleepTime, maxRetries));

        if (StringUtils.isNotEmpty(digest)) {
            builder.authorization("digest", digest.getBytes())
                    .aclProvider(new ACLProvider() {
                        @Override
                        public List<ACL> getDefaultAcl() {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }

                        @Override
                        public List<ACL> getAclForPath(String path) {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }
                    });
        }


        CuratorFramework curator = builder.build();

        curator.start();
        boolean connected = curator.blockUntilConnected(5, TimeUnit.SECONDS);
        if (!connected) {
            throw new IllegalStateException("Curator cant connect to the sever!");
        }
        return curator;

    }

    public static CuratorFramework make(String connectString) throws Exception {

        return make(connectString, null);
    }

    public static CuratorFramework make(String connectString, String digest) throws Exception {


        return make(connectString, digest, 60 * 1000, 10 * 1000, 500, 12);
    }

}
