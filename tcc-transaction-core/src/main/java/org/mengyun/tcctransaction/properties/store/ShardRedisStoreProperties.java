package org.mengyun.tcctransaction.properties.store;

import org.apache.commons.lang3.exception.ExceptionUtils;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nervose.Wu
 * @date 2022/5/24 17:44
 */
public class ShardRedisStoreProperties extends AbstractJedisStoreProperties {

    private static final Field DB;

    static {
        try {
            DB = JedisShardInfo.class.getDeclaredField("db");
            DB.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    private ShardedJedisPool instance;
    private List<HostAndPort> nodes = new ArrayList<>();

    public ShardedJedisPool getShardedJedisPool() {
        if (instance == null) {
            List<JedisShardInfo> shardInfos = nodes.stream()
                    .map(node -> {
                        JedisShardInfo info = new JedisShardInfo(node.getHost(),
                                node.getPort(),
                                getConnectionTimeout(),
                                getSoTimeout(),
                                1);
                        info.setPassword(getPassword());
                        try {
                            DB.set(info, node.getDatabase());
                        } catch (IllegalAccessException e) {
                            ExceptionUtils.rethrow(e);
                        }
                        return info;
                    })
                    .distinct()
                    .collect(Collectors.toList());
            instance = new ShardedJedisPool(getJedisPoolConfig(), shardInfos);
        }
        return instance;
    }

    public List<HostAndPort> getNodes() {
        return nodes;
    }

    public void setNodes(List<HostAndPort> nodes) {
        this.nodes = nodes;
    }
}
