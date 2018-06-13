package org.mengyun.tcctransaction.repository.helper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import javax.transaction.xa.Xid;

/**
 * Created by changming.xie on 9/15/16.
 */
public class RedisHelper {


    public static byte[] getRedisKey(String keyPrefix, Xid xid) {
        return new StringBuilder().append(keyPrefix).append(xid.toString()).toString().getBytes();
    }

    public static byte[] getRedisKey(String keyPrefix, String globalTransactionId, String branchQualifier) {
        return new StringBuilder().append(keyPrefix).append(globalTransactionId).append(":").append(branchQualifier).toString().getBytes();
    }

    public static byte[] getVersionKey(String keyPrefix, Xid xid) {
        return new StringBuilder().append("VER:").append(keyPrefix).append(xid.toString()).toString().getBytes();
    }

    public static byte[] getVersionKey(String keyPrefix, String globalTransactionId, String branchQualifier) {
        return new StringBuilder().append("VER:").append(keyPrefix).append(globalTransactionId).append(":").append(branchQualifier).toString().getBytes();
    }

    public static <T> T execute(JedisPool jedisPool, JedisCallback<T> callback) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return callback.doInJedis(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     *  扩展redis cluster
     *  @Method_Name             ：executeCluster
     *  @param jedisCluster
     *  @param callback
     *  @return T
     *  @Creation Date           ：2018/6/12
     *  @Author                  ：zc.ding@foxmail.com
     */
    public static <T> T execute(JedisCluster jedisCluster, JedisClusterCallback<T> callback) {
        try {
            return callback.doInJedisCluster(jedisCluster);
        } finally {
            if (jedisCluster != null) {
//                try {
//                    jedisCluster.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}