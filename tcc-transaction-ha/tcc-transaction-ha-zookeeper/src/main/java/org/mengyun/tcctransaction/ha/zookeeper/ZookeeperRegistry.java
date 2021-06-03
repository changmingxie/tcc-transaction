package org.mengyun.tcctransaction.ha.zookeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.mengyun.tcctransaction.ha.registry.Registration;
import org.mengyun.tcctransaction.ha.registry.Registry;
import org.mengyun.tcctransaction.ha.registry.RegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Lee on 2020/7/24 13:54.
 * aggregate-framework
 */
public class ZookeeperRegistry implements Registry {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperRegistry.class);


    private final CuratorFramework curator;
    private final ObjectMapper jackson;
    private final String root;


    public ZookeeperRegistry(CuratorFramework curator) {
        this(curator, new ObjectMapper());
    }

    public ZookeeperRegistry(CuratorFramework curator, ObjectMapper jackson) {

        this(curator, jackson, ZKpathConstants.REGISTRATION_ROOT);
    }


    public ZookeeperRegistry(CuratorFramework curator, ObjectMapper jackson, String root) {
        this.curator = curator;
        this.jackson = jackson;
        this.root = root;
    }


    @Override
    public void register(Registration registration) throws RegistryException {

        try {
            String path = ZKPaths.makePath(root, registration.getDomain());
            byte[] data = jackson.writeValueAsBytes(registration);
            int max = 2;
            boolean ok = false;
            for (int i = 0; i < max && !ok; i++) {
                try {
                    curator.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .forPath(path, data);
                    ok = true;
                    log.info("Succeed to register infrastructure [{}] for path {}", registration, path);
                } catch (KeeperException.NodeExistsException ignore) {
                    try {
                        curator.delete().forPath(path);
                    } catch (KeeperException.NodeExistsException ignore2) {

                    }
                }
            }
        } catch (Exception ex) {

            throw new RegistryException(ex);
        }
    }

    @Override
    public void deregister(Registration registration) throws RegistryException {
        String path = ZKPaths.makePath(root, registration.getDomain());
        try {
            curator.delete().forPath(path);
            log.info("Succeed to deregister infrastructure [{}] for path {}", registration, path);
        } catch (KeeperException.NoNodeException ignore) {
            //
        } catch (Exception ex) {
            throw new RegistryException(ex);
        }

    }


    @Override
    public void close() throws Exception {
        if (curator != null) {
            curator.close();
        }
    }
}
