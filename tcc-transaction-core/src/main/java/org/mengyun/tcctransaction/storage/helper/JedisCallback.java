package org.mengyun.tcctransaction.storage.helper;

import redis.clients.jedis.Jedis;

/**
 * Created by changming.xie on 9/15/16.
 */
public interface JedisCallback<T> {

    T doInJedis(Jedis jedis);
}
