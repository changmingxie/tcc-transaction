package org.mengyun.tcctransaction.repository.helper;

import org.mengyun.tcctransaction.utils.ByteUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

/**
 * Created by changming.xie on 9/15/16.
 */
public class RedisHelper {


    public static byte[] getRedisKey(String keyPrefix, Xid xid) {
        byte[] prefix = keyPrefix.getBytes();
        byte[] globalTransactionId = xid.getGlobalTransactionId();
        byte[] branchQualifier = xid.getBranchQualifier();

        byte[] key = new byte[prefix.length + globalTransactionId.length + branchQualifier.length];
        System.arraycopy(prefix, 0, key, 0, prefix.length);
        System.arraycopy(globalTransactionId, 0, key, prefix.length, globalTransactionId.length);
        System.arraycopy(branchQualifier, 0, key, prefix.length + globalTransactionId.length, branchQualifier.length);
        return key;
    }

    public static byte[] getKeyValue(JedisPool jedisPool, final byte[] key) {
        return execute(jedisPool, new JedisCallback<byte[]>() {
                    @Override
                    public byte[] doInJedis(Jedis jedis) {
                        return getKeyValue(jedis, key);
                    }
                }
        );
    }

    public static byte[] getKeyValue(Jedis jedis, final byte[] key) {

        Map<byte[], byte[]> fieldValueMap = jedis.hgetAll(key);

        List<Map.Entry<byte[], byte[]>> entries = new ArrayList<Map.Entry<byte[], byte[]>>(fieldValueMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<byte[], byte[]>>() {
            @Override
            public int compare(Map.Entry<byte[], byte[]> entry1, Map.Entry<byte[], byte[]> entry2) {
                return (int) (ByteUtils.bytesToLong(entry1.getKey()) - ByteUtils.bytesToLong(entry2.getKey()));
            }
        });

        if (entries.isEmpty()) {
            return null;
        }

        return entries.get(entries.size() - 1).getValue();
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
}