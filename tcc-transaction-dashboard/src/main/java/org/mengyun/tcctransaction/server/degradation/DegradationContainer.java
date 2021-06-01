package org.mengyun.tcctransaction.server.degradation;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.utils.ZKPaths;
import org.mengyun.tcctransaction.ha.zookeeper.ZKpathConstants;
import org.mengyun.tcctransaction.utils.ByteUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lee on 2020/9/23 17:30.
 * tcc-transaction
 */
@Component
@Slf4j
public class DegradationContainer {


    private final CuratorFramework curator;
    private final String root = ZKpathConstants.SENTINEL_CONTROL_ROOT;
    private final Map<String, Boolean> local = new ConcurrentHashMap<>();

    public DegradationContainer(CuratorFramework curator) {
        this.curator = curator;
    }

    /**
     * @param domain
     * @throws Exception
     */
    public void change(String domain, boolean degrade) throws Exception {
        String path = ZKPaths.makePath(root, domain);
        curator.setData().forPath(path, ByteUtils.bool2bytes(degrade));
    }

    @PostConstruct
    public void init() throws Exception {

        PathChildrenCache cache = new PathChildrenCache(curator, root, true);

        cache.getListenable()
                .addListener((client, event) -> {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                        case CHILD_UPDATED:
                            update(event.getData());
                            break;
                        case CHILD_REMOVED:
                            remove(event.getData());
                            break;
                    }
                });


        cache.start();

    }

    public void update(ChildData data) {
        String domain = ZKPaths.getNodeFromPath(data.getPath());
        local.put(domain, ByteUtils.bytes2bool(data.getData()));
    }

    public void remove(ChildData data) {
        String domain = ZKPaths.getNodeFromPath(data.getPath());
        local.remove(domain);
    }


    public Map<String, Boolean> getDomains() {
        return local;
    }
}
