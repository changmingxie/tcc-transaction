package org.mengyun.tcctransaction.repository.helper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * Created by changming.xie on 9/15/16.
 */
public interface JedisClusterCallback<T> {
    /**
    *  支持redis cluster
    *  @Method_Name             ：doInJedisCluster
    *  @param jedisCluster
    *  @return T
    *  @Creation Date           ：2018/6/12
    *  @Author                  ：zc.ding@foxmail.com
    */
    public T doInJedisCluster(JedisCluster jedisCluster);
}