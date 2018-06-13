package org.mengyun.tcctransaction.repository;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.helper.ExpandTransactionSerializer;
import org.mengyun.tcctransaction.repository.helper.JedisClusterCallback;
import org.mengyun.tcctransaction.repository.helper.JedisClusterExtend;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.serializer.JdkSerializationSerializer;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import javax.transaction.xa.Xid;
import java.util.*;

/**
 * Created by zc.ding on 2018/06/12
 */
public class RedisClusterTransactionRepository extends CachableTransactionRepository {

    static final Logger logger = Logger.getLogger(RedisClusterTransactionRepository.class.getSimpleName());

    private JedisCluster jedisCluster;
    
    private JedisClusterExtend jedisClusterExtend;

    private String keyPrefix = "TCC:";

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    private ObjectSerializer serializer = new JdkSerializationSerializer();

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    public void setJedisClusterExtend(JedisClusterExtend jedisClusterExtend) {
        this.jedisClusterExtend = jedisClusterExtend;
        this.jedisCluster = jedisClusterExtend.getJedisCluster();
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    protected int doCreate(final Transaction transaction) {
        try {
            Long statusCode = RedisHelper.execute(jedisCluster, new JedisClusterCallback<Long>() {
                @Override
                public Long doInJedisCluster(JedisCluster jedisCluster) {
                    List<byte[]> params = new ArrayList<byte[]>();
                    for (Map.Entry<byte[], byte[]> entry : ExpandTransactionSerializer.serialize(serializer, transaction).entrySet()) {
                        params.add(entry.getKey());
                        params.add(entry.getValue());
                    }
                    Object result = jedisCluster.eval("if redis.call('exists', KEYS[1]) == 0 then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;".getBytes(),
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
            Long statusCode = RedisHelper.execute(jedisCluster, new JedisClusterCallback<Long>() {
                @Override
                public Long doInJedisCluster(JedisCluster jedisCluster) {
                    transaction.updateTime();
                    transaction.updateVersion();
                    List<byte[]> params = new ArrayList<byte[]>();
                    for (Map.Entry<byte[], byte[]> entry : ExpandTransactionSerializer.serialize(serializer, transaction).entrySet()) {
                        params.add(entry.getKey());
                        params.add(entry.getValue());
                    }
                    Object result = jedisCluster.eval(String.format("if redis.call('hget',KEYS[1],'VERSION') == '%s' then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;",
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
            Long result = RedisHelper.execute(jedisCluster, new JedisClusterCallback<Long>() {
                @Override
                public Long doInJedisCluster(JedisCluster jedisCluster) {
                    return jedisCluster.del(RedisHelper.getRedisKey(keyPrefix, transaction.getXid()));
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
            Map<byte[], byte[]> content = RedisHelper.execute(jedisCluster, new JedisClusterCallback<Map<byte[], byte[]>>() {
                @Override
                public Map<byte[], byte[]> doInJedisCluster(JedisCluster jedisCluster) {
                    return jedisCluster.hgetAll(RedisHelper.getRedisKey(keyPrefix, xid));
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

    protected List<Transaction> doFindAll() {
        List<Transaction> list = new ArrayList<Transaction>();
        try {
            Set<byte[]> allKeys = new HashSet<byte[]>();
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            String pattern = keyPrefix + "*";
            for(String k : clusterNodes.keySet()){
                logger.debug("Getting keys from: " + pattern);
                JedisPool jp = clusterNodes.get(k);
                Jedis jedis = jp.getResource();
                try {
                    allKeys.addAll(jedis.keys(pattern.getBytes()));
                } catch(Exception e){
                    logger.error("Getting keys error: {}", e);
                } finally{
                    logger.debug("Connection closed.");
                    jedis.close();
                }
            }
            for (final byte[] key : allKeys) {
                Map<byte[], byte[]> map = jedisCluster.hgetAll(key);
                list.add(ExpandTransactionSerializer.deserialize(serializer, map));
            }
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
        return list;
    }
}