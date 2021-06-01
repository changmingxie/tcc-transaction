package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.repository.helper.RedisCommands;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.repository.helper.ShardHolder;
import org.mengyun.tcctransaction.repository.helper.ShardJedisCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

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
public class ShardJedisTransactionRepository extends AbstractRedisTransactionRepository {

    private ShardedJedisPool shardedJedisPool;

    public ShardedJedisPool getShardedJedisPool() {
        return this.shardedJedisPool;
    }

    public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;

        isSupportScan = RedisHelper.isSupportScanCommand(shardedJedisPool);
        if (!isSupportScan) {
            throw new RuntimeException("Redis not support 'scan' command, " +
                    "try update redis version higher than 2.8.0 ");
        }
    }

    @Override
    protected RedisCommands getRedisCommands(byte[] shardKey) {
        return new ShardJedisCommands(this.shardedJedisPool.getResource(), shardKey);
    }

    @Override
    protected ShardHolder<Jedis> getShardHolder() {

        return new ShardHolder() {

            ShardedJedis shardedJedis = shardedJedisPool.getResource();

            @Override
            public List<Jedis> getAllShards() {
                List<Jedis> allShards = new ArrayList<>();
                allShards.addAll(shardedJedis.getAllShards());
                allShards.sort(new JedisComparator());
                return allShards;
            }

            @Override
            public void close() throws IOException {

                if (shardedJedis != null) {
                    shardedJedis.close();
                }
            }
        };
    }
}
