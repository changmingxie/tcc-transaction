package org.mengyun.tcctransaction.storage.helper;

import org.mengyun.tcctransaction.api.Xid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Collection;
import java.util.Map;

/**
 * Created by changming.xie on 9/15/16.
 */
public class RedisHelper {

    public static final String SEPARATOR = ":";
    // 内置第二分割符，解决查询事件列表时Domain包含关系问题
    public static final String SECOND_SEPARATOR = "$$:";
    public static final String DELETED_KEY_PREFIX = "DELETE:";
    public static final String DOMAIN_KEY_PREFIX = "_TCCDOMAIN:";
    public static final int DELETED_KEY_KEEP_TIME = 3 * 24 * 3600;
    public static final String LEFT_BIG_BRACKET = "{";
    public static final String RIGHT_BIG_BRACKET = "}";
    private static final Logger log = LoggerFactory.getLogger(RedisHelper.class.getSimpleName());
    public static final int SCAN_COUNT = 30;
    public static final int SCAN_MIDDLE_COUNT = 1000;
    public static final String SCAN_TEST_PATTERN = "*";
    public static final String REDIS_SCAN_INIT_CURSOR = ShardOffset.SCAN_INIT_CURSOR;

    private RedisHelper() {
    }

    public static byte[] getDomainStoreRedisKey(String domain) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DOMAIN_KEY_PREFIX);
        stringBuilder.append(domain);
        if (!domain.endsWith(SEPARATOR)) {
            stringBuilder.append(SEPARATOR);
        }
        return stringBuilder.toString().getBytes();
    }

    public static byte[] getRedisKey(String keyPrefix, Xid xid) {
        return getRedisKeyString(keyPrefix, xid).getBytes();
    }

    public static String getRedisKeyString(String keyPrefix, Xid xid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(keyPrefix);
        if (!keyPrefix.endsWith(SEPARATOR)) {
            stringBuilder.append(SEPARATOR);
        }
        stringBuilder.append(SECOND_SEPARATOR);
        stringBuilder.append(xid.toString());
        return stringBuilder.toString();
    }

    /**
     * @param keyPrefix
     * @param xid
     * @return DELETE:{redisKey}
     */
    public static byte[] getDeletedRedisKey(String keyPrefix, Xid xid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DELETED_KEY_PREFIX);
        stringBuilder.append(LEFT_BIG_BRACKET);
        stringBuilder.append(getRedisKeyString(keyPrefix, xid));
        stringBuilder.append(RIGHT_BIG_BRACKET);
        return stringBuilder.toString().getBytes();
    }

    public static String getDeletedKeyPrefix(String domain) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DELETED_KEY_PREFIX);
        stringBuilder.append(LEFT_BIG_BRACKET);
        stringBuilder.append(getKeyPrefix(domain));
        return stringBuilder.toString();
    }

    public static String getKeyPrefix(String domain) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(domain);
        if (!domain.endsWith(SEPARATOR)) {
            stringBuilder.append(SEPARATOR);
        }
        stringBuilder.append(SECOND_SEPARATOR);
        return stringBuilder.toString();
    }

    public static <T> T execute(JedisPool jedisPool, JedisCallback<T> callback) {
        try (Jedis jedis = jedisPool.getResource()) {
            return callback.doInJedis(jedis);
        }
    }

    public static <T> T execute(ShardedJedisPool jedisPool, ShardedJedisCallback<T> callback) {
        try (ShardedJedis jedis = jedisPool.getResource()) {
            return callback.doInJedis(jedis);
        }
    }

    public static ScanParams buildDefaultScanParams(String pattern, int count) {
        return new ScanParams().match(pattern).count(count);
    }

    public static boolean isSupportScanCommand(Jedis jedis) {
        try {
            ScanParams scanParams = buildDefaultScanParams(SCAN_TEST_PATTERN, SCAN_COUNT);
            jedis.scan(REDIS_SCAN_INIT_CURSOR, scanParams);
        } catch (JedisDataException e) {
            log.error(e.getMessage(), e);
            log.info("Redis **NOT** support scan command");
            return false;
        }

        log.info("Redis support scan command");
        return true;
    }

    public static boolean isSupportScanCommand(JedisPool pool) {
        return execute(pool, RedisHelper::isSupportScanCommand);
    }

    public static boolean isSupportScanCommand(ShardedJedisPool shardedJedisPool) {
        Collection<Jedis> allShards = shardedJedisPool.getResource().getAllShards();

        for (Jedis jedis : allShards) {
            try {
                jedis.connect();
                if (!isSupportScanCommand(jedis)) {
                    return false;
                }
            } finally {
                if (jedis.isConnected()) {
                    jedis.disconnect();
                }
            }
        }

        return true;
    }

    public static boolean isSupportScanCommand(JedisCluster jedisCluster) {
        Map<String, JedisPool> jedisPoolMap = jedisCluster.getClusterNodes();

        for (Map.Entry<String, JedisPool> entry : jedisPoolMap.entrySet()) {
            if (!isSupportScanCommand(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    public static ScanParams scanArgs(String pattern, int count) {
        return new ScanParams().match(pattern).count(count);
    }
}
