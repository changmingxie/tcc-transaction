package org.mengyun.tcctransaction.repository;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.helper.ExpandTransactionSerializer;
import org.mengyun.tcctransaction.repository.helper.JedisCallback;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.serializer.KryoPoolSerializer;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;
import redis.clients.jedis.*;

import javax.transaction.xa.Xid;
import java.util.*;

/**
 * Created by changming.xie on 2/24/16.
 * <p/>
 * As the storage of transaction need safely durable,make sure the redis server is set as AOF mode and always fsync.
 * set below directives in your redis.conf
 * appendonly yes
 * appendfsync always
 */
public class RedisTransactionRepository extends CachableTransactionRepository {

    private static final Logger logger = Logger.getLogger(RedisTransactionRepository.class.getSimpleName());

    private JedisPool jedisPool;

    private String keyPrefix = "TCC:";

    private int fetchKeySize = 1000;

    private boolean isSupportScan = true;

    private boolean isForbiddenKeys = false;

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    private ObjectSerializer serializer = new KryoPoolSerializer();

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    public int getFetchKeySize() {
        return fetchKeySize;
    }

    public void setFetchKeySize(int fetchKeySize) {
        this.fetchKeySize = fetchKeySize;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        isSupportScan = RedisHelper.isSupportScanCommand(jedisPool.getResource());
        if (!isSupportScan && isForbiddenKeys) {
            throw new RuntimeException("Redis not support 'scan' command, " +
                    "and 'keys' command is forbidden, " +
                    "try update redis version higher than 2.8.0 " +
                    "or set 'isForbiddenKeys' to false");
        }
    }

    public void setSupportScan(boolean isSupportScan) {
        this.isSupportScan = isSupportScan;
    }

    public void setForbiddenKeys(boolean forbiddenKeys) {
        isForbiddenKeys = forbiddenKeys;
    }

    @Override
    protected int doCreate(final Transaction transaction) {


        try {
            Long statusCode = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {

                @Override
                public Long doInJedis(Jedis jedis) {


                    List<byte[]> params = new ArrayList<byte[]>();

                    for (Map.Entry<byte[], byte[]> entry : ExpandTransactionSerializer.serialize(serializer, transaction).entrySet()) {
                        params.add(entry.getKey());
                        params.add(entry.getValue());
                    }

                    Object result = jedis.eval("if redis.call('exists', KEYS[1]) == 0 then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;".getBytes(),
                            Arrays.asList(RedisHelper.getRedisKey(keyPrefix, transaction.getXid())), params);

                    return (Long) result;
                }
            });
            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(final Transaction transaction) {

        try {

            Long statusCode = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {

                    transaction.updateTime();
                    transaction.updateVersion();

                    List<byte[]> params = new ArrayList<byte[]>();

                    for (Map.Entry<byte[], byte[]> entry : ExpandTransactionSerializer.serialize(serializer, transaction).entrySet()) {
                        params.add(entry.getKey());
                        params.add(entry.getValue());
                    }

                    Object result = jedis.eval(String.format("if redis.call('hget',KEYS[1],'VERSION') == '%s' then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;",
                            transaction.getVersion() - 1).getBytes(),
                            Arrays.asList(RedisHelper.getRedisKey(keyPrefix, transaction.getXid())), params);

                    return (Long) result;
                }
            });

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(final Transaction transaction) {
        try {

            Long result = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {

                    return jedis.del(RedisHelper.getRedisKey(keyPrefix, transaction.getXid()));
                }
            });

            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected Transaction doFindOne(final Xid xid) {

        try {
            Long startTime = System.currentTimeMillis();
            Map<byte[], byte[]> content = RedisHelper.execute(jedisPool, new JedisCallback<Map<byte[], byte[]>>() {
                @Override
                public Map<byte[], byte[]> doInJedis(Jedis jedis) {
                    return jedis.hgetAll(RedisHelper.getRedisKey(keyPrefix, xid));
                }
            });
            logger.info("redis find cost time :" + (System.currentTimeMillis() - startTime));

            if (content != null && content.size() > 0) {
                return ExpandTransactionSerializer.deserialize(serializer, content);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected List<Transaction> doFindAllUnmodifiedSince(Date date) {

        List<Transaction> allTransactions = doFindAll();

        List<Transaction> allUnmodifiedSince = new ArrayList<Transaction>();

        for (Transaction transaction : allTransactions) {
            if (transaction.getLastUpdateTime().compareTo(date) < 0) {
                allUnmodifiedSince.add(transaction);
            }
        }

        return allUnmodifiedSince;
    }

    //    @Override
    protected List<Transaction> doFindAll() {

        try {

            final Set<byte[]> keys = RedisHelper.execute(jedisPool, new JedisCallback<Set<byte[]>>() {
                @Override
                public Set<byte[]> doInJedis(Jedis jedis) {

                    if (isSupportScan) {
                        List<String> allKeys = new ArrayList<String>();
                        String cursor = RedisHelper.SCAN_INIT_CURSOR;
                        ScanParams scanParams = RedisHelper.buildDefaultScanParams(keyPrefix + "*", fetchKeySize);
                        do {
                            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                            allKeys.addAll(scanResult.getResult());
                            cursor = scanResult.getStringCursor();
                        } while (!cursor.equals(RedisHelper.SCAN_INIT_CURSOR));

                        Set<byte[]> allKeySet = new HashSet<byte[]>();

                        for (String key : allKeys) {
                            allKeySet.add(key.getBytes());
                        }
                        logger.info(String.format("find all key by scan command with pattern:%s allKeySet.size()=%d", keyPrefix + "*", allKeySet.size()));
                        return allKeySet;
                    } else {
                        return jedis.keys((keyPrefix + "*").getBytes());
                    }

                }
            });


            return RedisHelper.execute(jedisPool, new JedisCallback<List<Transaction>>() {
                @Override
                public List<Transaction> doInJedis(Jedis jedis) {

                    Pipeline pipeline = jedis.pipelined();

                    for (final byte[] key : keys) {
                        pipeline.hgetAll(key);
                    }
                    List<Object> result = pipeline.syncAndReturnAll();

                    List<Transaction> list = new ArrayList<Transaction>();
                    for (Object data : result) {

                        if (data != null && ((Map<byte[], byte[]>) data).size() > 0) {

                            list.add(ExpandTransactionSerializer.deserialize(serializer, (Map<byte[], byte[]>) data));
                        }

                    }

                    return list;
                }
            });

        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }


}
