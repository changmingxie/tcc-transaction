package org.mengyun.tcctransaction.discovery.registry.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.mengyun.tcctransaction.discovery.registry.AbstractRegistryService;
import org.mengyun.tcctransaction.discovery.registry.RegistryConfig;
import org.mengyun.tcctransaction.exception.RegistryException;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 16:33
 */
public class ZookeeperRegistryServiceImpl extends AbstractRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistryServiceImpl.class.getSimpleName());

    private static final String BASE_PATH = "/tcc/server/";

    private CuratorFramework curator;

    private ZookeeperRegistryProperties properties;

    private String targetPath;

    private String registeredPath;

    public ZookeeperRegistryServiceImpl(RegistryConfig registryConfig) {
        setClusterName(registryConfig.getClusterName());
        this.targetPath = BASE_PATH + getClusterName();
        this.properties = registryConfig.getZookeeperRegistryProperties();
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(properties.getConnectString())
                .sessionTimeoutMs(properties.getSessionTimeout())
                .connectionTimeoutMs(properties.getConnectTimeout())
                .retryPolicy(new ExponentialBackoffRetry(properties.getBaseSleepTime(), properties.getMaxRetries()));

        if (StringUtils.isNotEmpty(properties.getDigest())) {
            builder.authorization("digest", properties.getDigest().getBytes())
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
        this.curator = builder.build();
    }

    @Override
    public void start() {
        curator.start();
        boolean connected;
        try {
            connected = curator.blockUntilConnected(properties.getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RegistryException("Cant connect to the zookeeper", e);
        }
        if (!connected) {
            throw new RegistryException("Cant connect to the zookeeper");
        }
    }

    @Override
    protected void doRegister(InetSocketAddress address) throws Exception {
        createParentNode();

        registeredPath = curator.create()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(targetPath + "/node", NetUtils.parseSocketAddress(address).getBytes());
        logger.info("Registered with zookeeper");

        curator.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.RECONNECTED) {
                try {
                    if (curator.checkExists().forPath(registeredPath) == null) {
                        registeredPath = curator.create()
                                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                                .forPath(targetPath + "/node", NetUtils.parseSocketAddress(address).getBytes());
                        logger.info("Re-registered with zookeeper");
                    }
                } catch (Exception e) {
                    logger.warn("Cant connect to the zookeeper");
                }
            }
        });
    }

    @Override
    protected void doSubscribe() throws Exception {
        createParentNode();

        PathChildrenCache pathChildrenCache = new PathChildrenCache(curator, targetPath, false);
        pathChildrenCache.getListenable().addListener((curator, pathChildrenCacheEvent) -> {
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                case CHILD_REMOVED:
                case CHILD_UPDATED:
                    try {
                        updateServiceList();
                    } catch (Exception e) {
                        logger.warn("Failed to update server addresses", e);
                    }
                    break;
                default:
                    break;
            }
        });
        pathChildrenCache.start();
        updateServiceList();
    }

    @Override
    public void close() {
        try {
            curator.close();
        } catch (Exception e) {
            //ignore
        }
    }

    private void updateServiceList() throws Exception {
        List<String> nodePaths = curator.getChildren().forPath(targetPath);
        List<InetSocketAddress> newServerAddresses = new ArrayList<>();
        for (String nodePath : nodePaths) {
            InetSocketAddress inetSocketAddress = NetUtils.toInetSocketAddress(new String(curator.getData().forPath(targetPath + "/" + nodePath), StandardCharsets.UTF_8));
            newServerAddresses.add(inetSocketAddress);
        }
        setServerAddresses(newServerAddresses);
    }

    private void createParentNode() throws Exception {
        if (curator.checkExists().forPath(targetPath) == null) {
            try {
                curator.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(targetPath, "".getBytes());
            } catch (KeeperException.NodeExistsException ignore) {
            }
        }
    }
}
