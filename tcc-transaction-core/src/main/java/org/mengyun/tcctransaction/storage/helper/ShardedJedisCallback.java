package org.mengyun.tcctransaction.storage.helper;

import redis.clients.jedis.ShardedJedis;

public interface ShardedJedisCallback<T> {

    public T doInJedis(ShardedJedis jedis);
}
