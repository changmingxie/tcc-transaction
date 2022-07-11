package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.storage.helper.JedisClusterCommands;
import org.mengyun.tcctransaction.storage.helper.RedisCommands;
import org.mengyun.tcctransaction.storage.helper.ShardHolder;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class JedisClusterTransactionStorage extends AbstractRedisTransactionStorage {

    private JedisCluster jedisCluster;

    public JedisClusterTransactionStorage(TransactionStoreSerializer serializer, StoreConfig storeConfig) {
        super(serializer, storeConfig);
        this.jedisCluster = storeConfig.getJedisCluster();
    }

    @Override
    protected RedisCommands getRedisCommands(byte[] shardKey) {
        return new JedisClusterCommands(jedisCluster);
    }

    @Override
    protected ShardHolder<Jedis> getShardHolder() {
        return new JedisClusterShardHolder();
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    class JedisClusterShardHolder implements ShardHolder<Jedis> {

        private static final int MASTER_NODE_INDEX = 2;

        private final List<Jedis> allShards = new ArrayList<>();

        @Override
        public void close() throws IOException {

            for (Jedis jedis : allShards) {
                try {
                    if (jedis != null) {
                        jedis.close();
                    }
                } catch (Exception e) {
                    log.error("close jedis error", e);
                }
            }
        }

        @Override
        public List<Jedis> getAllShards() {

            if (allShards.isEmpty()) {

                Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();

                Set<String> masterNodeKeys = getMasterNodeKeys(clusterNodes);

                for (String masterNodeKey : masterNodeKeys) {

                    JedisPool jedisPool = clusterNodes.get(masterNodeKey);

                    if (jedisPool != null) {
                        Jedis jedis = clusterNodes.get(masterNodeKey).getResource();
                        allShards.add(jedis);
                    }
                }

                allShards.sort(new AbstractRedisTransactionStorage.JedisComparator());
            }

            return allShards;
        }

        private Set<String> getMasterNodeKeys(Map<String, JedisPool> clusterNodes) {
            Set<String> masterNodeKeys = new HashSet<>();

            for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {

                try (Jedis jedis = entry.getValue().getResource()) {

                    List<Object> slots = jedis.clusterSlots();

                    for (Object slotInfoObj : slots) {
                        List<Object> slotInfo = (List<Object>) slotInfoObj;

                        if (slotInfo.size() <= MASTER_NODE_INDEX) {
                            continue;
                        }

                        // hostInfos
                        List<Object> hostInfos = (List<Object>) slotInfo.get(MASTER_NODE_INDEX);
                        if (hostInfos.isEmpty()) {
                            continue;
                        }

                        // at this time, we just use master, discard slave information
                        HostAndPort node = generateHostAndPort(hostInfos);
                        masterNodeKeys.add(JedisClusterInfoCache.getNodeKey(node));
                    }

                    break;
                } catch (Exception e) {
                    // try next jedispool
                }
            }
            return masterNodeKeys;
        }

        private HostAndPort generateHostAndPort(List<Object> hostInfos) {
            String host = encode((byte[]) hostInfos.get(0));
            int port = ((Long) hostInfos.get(1)).intValue();
            return new HostAndPort(host, port);
        }


        private String encode(final byte[] data) {
            try {
                return new String(data, Protocol.CHARSET);
            } catch (UnsupportedEncodingException e) {
                throw new JedisException(e);
            }
        }
    }

}

