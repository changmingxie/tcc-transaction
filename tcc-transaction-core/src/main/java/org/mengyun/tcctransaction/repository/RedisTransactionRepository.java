package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.repository.helper.JedisCommands;
import org.mengyun.tcctransaction.repository.helper.RedisCommands;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.repository.helper.ShardHolder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by changming.xie on 2/24/16.
 * <p/>
 * As the storage of transaction need safely durable,make sure the redis server is set as AOF mode and always fsync.
 * set below directives in your redis.conf
 * appendonly yes
 * appendfsync always
 */
public class RedisTransactionRepository extends AbstractRedisTransactionRepository {

    private JedisPool jedisPool;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        isSupportScan = RedisHelper.isSupportScanCommand(jedisPool);
        if (!isSupportScan) {
            throw new RuntimeException("Redis not support 'scan' command, " +
                    "try update redis version higher than 2.8.0 ");
        }
    }

    @Override
    protected ShardHolder<Jedis> getShardHolder() {

        return new ShardHolder() {

            private List<Jedis> allShards = new ArrayList<>();

            @Override
            public List<Jedis> getAllShards() {
                if (allShards.isEmpty()) {
                    allShards.add(jedisPool.getResource());
                }

                return allShards;
            }

            @Override
            public void close() throws IOException {
                for (Jedis jedis : allShards) {
                    jedis.close();
                }
            }
        };
    }

    @Override
    protected RedisCommands getRedisCommands(byte[] shardKey) {
        return new JedisCommands(jedisPool.getResource());
    }
}
