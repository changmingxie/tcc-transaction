package org.mengyun.tcctransaction.storage.helper;

import redis.clients.jedis.Pipeline;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PipelineCommands implements RedisCommands {

    private Pipeline pipeline;

    public PipelineCommands(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public Object eval(byte[] scripts, List<byte[]> keys, List<byte[]> args) {
        pipeline.eval(scripts, keys, args);
        return null;
    }

    @Override
    public Long del(byte[] key) {
        pipeline.del(key);
        return null;
    }

    @Override
    public Map<byte[], byte[]> hgetAll(byte[] key) {
        pipeline.hgetAll(key);
        return null;
    }

    @Override
    public void hset(byte[] key, byte[] field, byte[] value) {
        pipeline.hset(key, field, value);
    }

    @Override
    public void hdel(byte[] key, byte[] field) {
        pipeline.hdel(key, field);
    }

    @Override
    public void expire(byte[] key, int expireTime) {
        pipeline.expire(key, expireTime);
    }

    @Override
    public Long renamenx(byte[] oldkey, byte[] newkey) {
        return pipeline.renamenx(oldkey, newkey).get();
    }

    @Override
    public List<Object> executePipelined(CommandCallback commandCallback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
    }
}
