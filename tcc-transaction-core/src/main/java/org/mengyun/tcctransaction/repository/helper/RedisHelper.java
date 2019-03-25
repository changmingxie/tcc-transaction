package org.mengyun.tcctransaction.repository.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.transaction.xa.Xid;

/**
 * Created by changming.xie on 9/15/16.
 */
public class RedisHelper {

    public static int    SCAN_COUNT = 30;
    public static String SCAN_TEST_PATTERN = "*";
    public static String SCAN_INIT_CURSOR  = "0";

    private static final Logger logger = LoggerFactory.getLogger(RedisHelper.class);

    public static byte[] getRedisKey(String keyPrefix, Xid xid) {
        return new StringBuilder().append(keyPrefix).append(xid.toString()).toString().getBytes();
    }

    public static byte[] getRedisKey(String keyPrefix, String globalTransactionId, String branchQualifier) {

        if (keyPrefix.startsWith("AGG")) {
            return new StringBuilder().append(keyPrefix)
                    .append("globalTransactionId").append(":").append(globalTransactionId).append(",")
                    .append("branchQualifier").append(":").append(branchQualifier).toString().getBytes();
        } else {
            return new StringBuilder().append(keyPrefix)
                    .append(globalTransactionId).append(":")
                    .append(branchQualifier).toString().getBytes();
        }

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

    public static ScanParams buildDefaultScanParams(String pattern, int count) {
        return new ScanParams().match(pattern).count(count);
    }

    public static Boolean isSupportScanCommand(Jedis jedis) {
        try {
            ScanParams scanParams = buildDefaultScanParams(SCAN_TEST_PATTERN, SCAN_COUNT);
            jedis.scan(SCAN_INIT_CURSOR, scanParams);
        } catch (JedisDataException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            logger.info("Redis **NOT** support scan command");
            return false;
        }

        logger.info("Redis support scan command");
        return true;
    }

    static public Boolean isSupportScanCommand(JedisPool pool) {
        return execute(pool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {
                return isSupportScanCommand(jedis);
            }
        });
    }
}