package org.mengyun.tcctransaction.storage.helper;

import redis.clients.jedis.ShardedJedisPipeline;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ShardedPipelineCommand implements RedisCommands {

    private ShardedJedisPipeline shardedJedisPipeline;

    public ShardedPipelineCommand(ShardedJedisPipeline shardedJedisPipeline) {
        this.shardedJedisPipeline = shardedJedisPipeline;
    }

    @Override
    public Object eval(byte[] scripts, List<byte[]> keys, List<byte[]> args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long del(byte[] key) {
        this.shardedJedisPipeline.del(key);
        return null;
    }

    @Override
    public Map<byte[], byte[]> hgetAll(byte[] key) {
        this.shardedJedisPipeline.hgetAll(key);
        return null;
    }

    @Override
    public void hset(byte[] key, byte[] field, byte[] value) {
        this.shardedJedisPipeline.hset(key, field, value);
    }

    @Override
    public void hdel(byte[] key, byte[] field) {
        this.shardedJedisPipeline.hdel(key, field);
    }

    @Override
    public void expire(byte[] key, int expireTime) {
        this.shardedJedisPipeline.expire(key, expireTime);
    }

    @Override
    public Long renamenx(byte[] oldkey, byte[] newkey) {
        // FIXME
        throw new RuntimeException("TODO");
    }

    @Override
    public List<Object> executePipelined(CommandCallback commandCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {

    }
}
