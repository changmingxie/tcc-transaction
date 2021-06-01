package org.mengyun.tcctransaction.repository.helper;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface RedisCommands extends Closeable {

    Object eval(byte[] scripts, List<byte[]> keys, List<byte[]> args);

    Long del(byte[] key);

    Map<byte[], byte[]> hgetAll(byte[] key);

    void hset(byte[] key, byte[] field, byte[] value);

    void hdel(byte[] key, byte[] field);

    void expire(byte[] key, int expireTime);

    List<Object> executePipelined(CommandCallback<List<Object>> commandCallback);
}
