package org.mengyun.tcctransaction.ha.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.mengyun.tcctransaction.ha.SentinelController;
import org.mengyun.tcctransaction.utils.ByteUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZookeeperSentinelController implements SentinelController {

    /**
     * false 不需要降级
     */
    private final AtomicBoolean degrade = new AtomicBoolean(false);
    private final CuratorFramework curator;
    private final String root;

    private String domain;


    public ZookeeperSentinelController(CuratorFramework curator, String domain, String root) {

        if (curator.getState() != CuratorFrameworkState.STARTED)
            throw new IllegalStateException("Curator may not initialized");
        this.curator = curator;
        this.root = root;
        this.domain = domain;
    }

    public ZookeeperSentinelController(CuratorFramework curator, String domain) {
        this(curator, domain, ZKpathConstants.SENTINEL_CONTROL_ROOT);
    }

    @Override
    public boolean degrade() {

        return degrade.get();
    }

    @PostConstruct
    public void init() throws Exception {


        String path = ZKPaths.makePath(root, domain);

        setDefaultIfNecessary(path);


        NodeCache cache = new NodeCache(curator, path);

        cache.getListenable()
                .addListener(new NodeCacheListener() {
                    @Override
                    public void nodeChanged() throws Exception {
                        ChildData data = cache.getCurrentData();
                        ZookeeperSentinelController.this.change(data.getData());
                    }
                });

        cache.start();

    }

    /**
     * 初次写入,如果zk上没有该节点，创建该节点并初写入false
     *
     * @throws Exception 23
     */
    private void setDefaultIfNecessary(String path) throws Exception {


        Stat stat = curator.checkExists().forPath(path);

        if (stat == null) {
            // 说明数据不存在,向zk 写入 FALSE
            curator.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, ByteUtils.FALSE);
        }
    }

    private void change(byte[] data) {
        this.degrade.set(ByteUtils.bytes2bool(data));
    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
