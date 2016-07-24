package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.serializer.JdkSerializationSerializer;
import org.mengyun.tcctransaction.serializer.KryoTransactionSerializer;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;
import org.mengyun.tcctransaction.utils.ByteUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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

    private JedisPool jedisPool;

    private String keyPrefix = "tcc_";

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    private ObjectSerializer serializer = new JdkSerializationSerializer();

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected int doCreate(final Transaction transaction) {

        try {
            final byte[] key = getRedisKey(transaction.getXid());
            Long statusCode = execute(new JedisCallback<Long>() {

                @Override
                public Long doInJedis(Jedis jedis) {
                    return jedis.hsetnx(key, ByteUtils.longToBytes(transaction.getVersion()), serializer.serialize(transaction));
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
            final byte[] key = getRedisKey(transaction.getXid());
            Long statusCode = execute(new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    transaction.updateTime();
                    transaction.updateVersion();
                    return jedis.hsetnx(key, ByteUtils.longToBytes(transaction.getVersion()), serializer.serialize(transaction));
                }
            });

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(Transaction transaction) {
        try {
            final byte[] key = getRedisKey(transaction.getXid());
            Long result = execute(new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    return jedis.del(key);
                }
            });
            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected Transaction doFindOne(Xid xid) {

        try {
            final byte[] key = getRedisKey(xid);
            byte[] content = execute(new JedisCallback<byte[]>() {
                @Override
                public byte[] doInJedis(Jedis jedis) {
                    return getKeyValue(key);
                }
            });

            if (content != null) {
                return (Transaction) serializer.deserialize(content);
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
            if (transaction.getTransactionType().equals(TransactionType.ROOT)
                    && transaction.getLastUpdateTime().compareTo(date) < 0) {
                allUnmodifiedSince.add(transaction);
            }
        }

        return allUnmodifiedSince;
    }

    //    @Override
    protected List<Transaction> doFindAll() {

        try {
            List<Transaction> transactions = new ArrayList<Transaction>();
            Set<byte[]> keys = execute(new JedisCallback<Set<byte[]>>() {
                @Override
                public Set<byte[]> doInJedis(Jedis jedis) {
                    return jedis.keys((keyPrefix + "*").getBytes());
                }
            });

            for (final byte[] key : keys) {
                byte[] content = getKeyValue(key);

                if (content != null) {
                    transactions.add((Transaction) serializer.deserialize(content));
                }
            }

            return transactions;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    private byte[] getKeyValue(final byte[] key) {
        return execute(new JedisCallback<byte[]>() {
                           @Override
                           public byte[] doInJedis(Jedis jedis) {

                               Map<byte[], byte[]> fieldValueMap = jedis.hgetAll(key);

                               List<Map.Entry<byte[], byte[]>> entries = new ArrayList<Map.Entry<byte[], byte[]>>(fieldValueMap.entrySet());
                               Collections.sort(entries, new Comparator<Map.Entry<byte[], byte[]>>() {
                                   @Override
                                   public int compare(Map.Entry<byte[], byte[]> entry1, Map.Entry<byte[], byte[]> entry2) {
                                       return (int) (ByteUtils.bytesToLong(entry1.getKey()) - ByteUtils.bytesToLong(entry2.getKey()));
                                   }
                               });


                               byte[] content = entries.get(entries.size() - 1).getValue();

                               return content;
                           }
                       }
        );
    }

    private byte[] getRedisKey(Xid xid) {
        byte[] prefix = keyPrefix.getBytes();
        byte[] globalTransactionId = xid.getGlobalTransactionId();
        byte[] branchQualifier = xid.getBranchQualifier();

        byte[] key = new byte[prefix.length + globalTransactionId.length + branchQualifier.length];
        System.arraycopy(prefix, 0, key, 0, prefix.length);
        System.arraycopy(globalTransactionId, 0, key, prefix.length, globalTransactionId.length);
        System.arraycopy(branchQualifier, 0, key, prefix.length + globalTransactionId.length, branchQualifier.length);
        return key;
    }

    private <T> T execute(JedisCallback<T> callback) {
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

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    interface JedisCallback<T> {

        public T doInJedis(Jedis jedis);
    }
}
