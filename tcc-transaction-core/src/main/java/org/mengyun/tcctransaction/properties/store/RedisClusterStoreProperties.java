package org.mengyun.tcctransaction.properties.store;

import redis.clients.jedis.JedisCluster;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Nervose.Wu
 * @date 2022/5/24 17:44
 */
public class RedisClusterStoreProperties extends AbstractJedisStoreProperties {

    private Set<HostAndPort> nodes = new LinkedHashSet<>();
    private int maxAttempts = 5;
    private JedisCluster instance;

    public JedisCluster getCluster() {
        if (instance == null) {
            Set<redis.clients.jedis.HostAndPort> hps = nodes.stream()
                    .map(hp -> new redis.clients.jedis.HostAndPort(hp.getHost(), hp.getPort()))
                    .collect(Collectors.toSet());
            instance = new JedisCluster(hps, getConnectionTimeout(), getSoTimeout(), maxAttempts, getPassword(), null, getJedisPoolConfig());
        }
        return instance;
    }

    public Set<HostAndPort> getNodes() {
        return nodes;
    }

    public void setNodes(Set<HostAndPort> nodes) {
        this.nodes = nodes;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
