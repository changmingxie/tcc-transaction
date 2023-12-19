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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 16:33
 */
public class ZookeeperRegistryServiceImpl extends AbstractRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistryServiceImpl.class.getSimpleName());

    private static final String BASE_PATH = "/tcc/server/";

    private static final String BASE_PATH_FOR_DASHBOARD = "/tcc/server-for-dashboard/";

    private ZookeeperRegistryProperties properties;

    private String targetPath;

    private String targetPathForDashboard;

    private ZookeeperInstance instance;

    private ZookeeperInstance backupInstance;

    public ZookeeperRegistryServiceImpl(RegistryConfig registryConfig) {
        setClusterName(registryConfig.getClusterName());
        this.targetPath = BASE_PATH + getClusterName();
        this.targetPathForDashboard = BASE_PATH_FOR_DASHBOARD + getClusterName();
        this.properties = registryConfig.getZookeeperRegistryProperties();
        this.instance = new ZookeeperInstance(properties.getConnectString(), properties.getDigest(), buildCurator(false));
        if (!StringUtils.isEmpty(properties.getBackupConnectString()) && !Objects.equals(properties.getConnectString(), properties.getBackupConnectString())) {
            this.backupInstance = new ZookeeperInstance(properties.getBackupConnectString(), properties.getBackupDigest(), buildCurator(true));
        }
    }

    private CuratorFramework buildCurator(boolean isBackup) {
        String connectString = isBackup ? properties.getBackupConnectString() : properties.getConnectString();
        String digest = isBackup ? properties.getBackupDigest() : properties.getDigest();

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(properties.getSessionTimeout())
                .connectionTimeoutMs(properties.getConnectTimeout())
                .retryPolicy(new ExponentialBackoffRetry(properties.getBaseSleepTime(), properties.getMaxRetries()));

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
        return builder.build();
    }

    @Override
    public void start() {
        start(instance);
        if (backupInstance != null) {
            start(backupInstance);
        }
    }

    private void start(ZookeeperInstance target) {
        CuratorFramework curator = target.getCurator();
        curator.start();
        boolean connected = false;
        try {
            connected = curator.blockUntilConnected(properties.getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //ignore
        }
        if (!connected) {
            logger.error("Cant connect to the zookeeper {}", target.getConnectString());
        }
    }

    @Override
    protected void doRegister(InetSocketAddress address, InetSocketAddress addressForDashboard) {
        doRegister(address, addressForDashboard, instance);
        if (backupInstance != null) {
            doRegister(address, addressForDashboard, backupInstance);
        }
    }

    private void doRegister(InetSocketAddress address, InetSocketAddress addressForDashboard, ZookeeperInstance target) {
        CuratorFramework curator = target.getCurator();

        try {
            createParentNode(curator, false);
            target.setRegisteredPath(curator.create()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(targetPath + "/node", NetUtils.parseSocketAddress(address).getBytes()));

            createParentNode(curator, true);
            target.setRegisteredPathForDashboard(curator.create()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(targetPathForDashboard + "/node", NetUtils.parseSocketAddress(addressForDashboard).getBytes()));
            logger.info("Registered with zookeeper. {},{}", address, addressForDashboard);
        } catch (Exception e) {
            logger.error("Failed to register with zookeeper", e);
        }

        curator.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.RECONNECTED || newState == ConnectionState.CONNECTED) {
                try {
                    createParentNode(curator, false);
                    if (target.getRegisteredPath() == null || curator.checkExists().forPath(target.getRegisteredPath()) == null) {
                        target.setRegisteredPath(curator.create()
                                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                                .forPath(targetPath + "/node", NetUtils.parseSocketAddress(address).getBytes()));
                    }

                    createParentNode(curator, true);
                    if (target.getRegisteredPathForDashboard() == null || curator.checkExists().forPath(target.getRegisteredPathForDashboard()) == null) {
                        target.setRegisteredPathForDashboard(curator.create()
                                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                                .forPath(targetPathForDashboard + "/node", NetUtils.parseSocketAddress(addressForDashboard).getBytes()));
                    }

                    if (newState == ConnectionState.CONNECTED) {
                        logger.info("Registered with zookeeper. {},{}", address, addressForDashboard);
                    } else {
                        logger.info("Re-registered with zookeeper");
                    }
                } catch (Exception e) {
                    logger.error("Failed to register with zookeeper", e);
                }
            }
        });
    }

    @Override
    protected void doSubscribe(boolean addressForDashboard) {
        doSubscribe(instance, addressForDashboard);
        if (backupInstance != null) {
            doSubscribe(backupInstance, addressForDashboard);
        }
    }

    private void doSubscribe(ZookeeperInstance target, boolean addressForDashboard) {
        try {
            createParentNode(target.getCurator(), addressForDashboard);
        } catch (Exception e) {
            //ignore
        }

        String path = addressForDashboard ? targetPathForDashboard : targetPath;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(target.getCurator(), path, false);
        pathChildrenCache.getListenable().addListener((curator, pathChildrenCacheEvent) -> {
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                case CHILD_REMOVED:
                case CHILD_UPDATED:
                    try {
                        updateServiceList(target.getCurator(), addressForDashboard);
                    } catch (Exception e) {
                        logger.warn("Failed to update server addresses", e);
                    }
                    break;
                default:
                    break;
            }
        });
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RegistryException("Failed to subscribe", e);
        }
        try {
            updateServiceList(target.getCurator(), addressForDashboard);
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void close() {
        close(instance);
        if (backupInstance != null) {
            close(backupInstance);
        }
    }

    private void close(ZookeeperInstance target) {
        // manually remove ephemeral paths to avoid delays
        try {
            if(StringUtils.isNotEmpty(target.getRegisteredPath())){
                target.getCurator().delete().forPath(target.getRegisteredPath());
            }
            if(StringUtils.isNotEmpty(target.getRegisteredPathForDashboard())){
                target.getCurator().delete().forPath(target.getRegisteredPathForDashboard());
            }
        } catch (Exception e) {
            //ignore
        }
        try {
            target.getCurator().close();
        } catch (Exception e) {
            //ignore
        }
    }

    private void updateServiceList(CuratorFramework curator, boolean addressForDashboard) throws Exception {
        String path = addressForDashboard ? targetPathForDashboard : targetPath;
        List<String> nodePaths = curator.getChildren().forPath(path);
        List<String> newServerAddresses = new ArrayList<>();
        for (String nodePath : nodePaths) {
            newServerAddresses.add(new String(curator.getData().forPath(path + "/" + nodePath), StandardCharsets.UTF_8));
        }
        setServerAddresses(newServerAddresses, addressForDashboard);
    }

    private void createParentNode(CuratorFramework target, boolean addressForDashboard) throws Exception {
        String path = addressForDashboard ? targetPathForDashboard : targetPath;
        if (target.checkExists().forPath(path) == null) {
            try {
                target.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(path, "".getBytes());
            } catch (KeeperException.NodeExistsException ignore) {
            }
        }
    }
}
