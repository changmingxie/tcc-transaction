package org.mengyun.tcctransaction.server.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.mengyun.tcctransaction.ha.registry.Registration;
import org.mengyun.tcctransaction.ha.zookeeper.ZKpathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import redis.clients.jedis.exceptions.JedisException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Lee on 2020/6/29 16:49.
 * aggregate-framework
 */

@Slf4j
@Component
public class RegistrationContainer implements SmartLifecycle, ApplicationEventPublisherAware {


    private final AtomicBoolean running = new AtomicBoolean(false);
    @Getter
    @Setter
    private String root = ZKpathConstants.REGISTRATION_ROOT;
    @Autowired
    private CuratorFramework curator;
    @Autowired
    private ObjectMapper _jackson;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    @SneakyThrows
    public void start() {

        if (running.compareAndSet(false, true)) {
            // 缓存数据
            PathChildrenCache cache = new PathChildrenCache(curator, root, true);

            cache.getListenable()
                    .addListener((client, event) -> {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                            case CHILD_UPDATED:
                                handleModified(event.getData());
                                break;
                            case CHILD_REMOVED:
                                handleRemoved(event.getData());
                                break;
                        }

                    });

            cache.start();
        }

    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            // nothing to do
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }


    private void handleModified(ChildData data) throws Exception {

        try {

            Registration registration = _jackson.readValue(data.getData(), Registration.class);
            publish(registration);

        } catch (JedisException jce) {

            ExceptionUtils.rethrow(jce);

        }


    }

    private void handleRemoved(ChildData data) {
        String path = data.getPath();
        publish(ZKPaths.getNodeFromPath(path));
    }

    /**
     * 添加一个
     *
     * @param registration
     * @throws Exception
     */
    public void add(Registration registration) throws Exception {
        String path = ZKPaths.makePath(root, registration.getDomain());
        byte[] data = _jackson.writeValueAsBytes(registration);
        int max = 2;
        boolean ok = false;
        for (int i = 0; i < max && !ok; i++) {
            try {
                curator.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(path, data);
                ok = true;
            } catch (KeeperException.NodeExistsException ignore) {
                curator.delete().forPath(path);
            }
        }
    }

    public void remove(String domain) throws Exception {

        String path = ZKPaths.makePath(root, domain);
        try {
            curator.delete().forPath(path);
        } catch (KeeperException.NoNodeException ignore) {

        }

    }


    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }


    private void publish(Object event) {
        applicationEventPublisher.publishEvent(event);
    }


}
