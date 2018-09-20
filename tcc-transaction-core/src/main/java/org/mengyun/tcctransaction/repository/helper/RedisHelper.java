package org.mengyun.tcctransaction.repository.helper;

import org.mengyun.tcctransaction.utils.RedisUtils;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by changming.xie on 9/15/16.
 */
public class RedisHelper {


    static final Logger logger = LoggerFactory.getLogger(RedisHelper.class);

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

    public static boolean isSupportScanCommand(Jedis jedis) {

        if (jedis == null) {
            logger.info("jedis is null,");
            return false;
        }

        String serverInfo = jedis.info("Server");

        int versionIndex = serverInfo.indexOf("redis_version");

        String infoWithVersionAhead = serverInfo.substring(versionIndex);

        int versionOverIndex = infoWithVersionAhead.indexOf("\r");

        String serverVersion = infoWithVersionAhead.substring(0, versionOverIndex);

        String leastVersionForScan = "redis_version:2.8";

        if (StringUtils.isNotEmpty(serverVersion)) {

            logger.info("redis server:{}", serverVersion);

            return serverVersion.compareTo(leastVersionForScan) >= 0;
        } else {
            return false;
        }
    }

    public static List<byte[]> getAllKeys(JedisPool jedisPool, final String keyPattern) {

        return RedisHelper.execute(jedisPool, new JedisCallback<List<byte[]>>() {
            @Override
            public List<byte[]> doInJedis(Jedis jedis) {

                if (isSupportScanCommand(jedis)) {

                    List<String> allKeys = new ArrayList<String>();

                    String cursor = "0";
                    do {
                        ScanResult<String> scanResult = jedis.scan(cursor, new ScanParams().match(keyPattern).count(RedisUtils.DEFAULT_FETCH_KEY_SIZE));
                        allKeys.addAll(scanResult.getResult());
                        cursor = scanResult.getStringCursor();
                    } while (!cursor.equals("0"));

                    List<byte[]> allKeySet = new ArrayList<byte[]>();

                    for (String key : allKeys) {
                        allKeySet.add(key.getBytes());
                    }
                    logger.info(String.format("find all key by scan command with pattern:%s allKeySet.size()=%d", keyPattern, allKeySet.size()));
                    return allKeySet;
                } else {
                    Set<byte[]> keySet = jedis.keys(keyPattern.getBytes());
                    return new ArrayList<byte[]>(keySet);
                }

            }
        });
    }
}