package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.utils.SerializationUtils;
import redis.clients.jedis.Jedis;

import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by changming.xie on 2/24/16.
 *
 * As the storage of transaction need safely durable,make sure the redis server is set as AOF mode and always fsync.
 * set below directives in your redis.conf
 * appendonly yes
 * appendfsync always
 */
public class RedisTransactionRepository extends CachableTransactionRepository {

    private String host = "localhost";

    private int port = 6379;

    private int connectionTimeout = 2000;

    private int soTimeout = 2000;

    private Jedis jedis;

    private String keyPrefix = "tcc_";

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    protected Jedis getJedis() {

        if (jedis == null) {
            synchronized (RedisTransactionRepository.class) {
                if (jedis == null) {
                    jedis = new Jedis(host, port, connectionTimeout, soTimeout);
                }
            }
        }
        return jedis;
    }

    @Override
    protected void doCreate(Transaction transaction) {

        try {
            byte[] key = getRedisKey(transaction.getXid());
            getJedis().set(key, SerializationUtils.serialize(transaction));
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }


    @Override
    protected void doUpdate(Transaction transaction) {
        try {
            byte[] key = getRedisKey(transaction.getXid());
            getJedis().set(key, SerializationUtils.serialize(transaction));
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected void doDelete(Transaction transaction) {
        try {
            byte[] key = getRedisKey(transaction.getXid());
            getJedis().del(key);
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected Transaction doFindOne(Xid xid) {

        try {
            byte[] key = getRedisKey(xid);
            byte[] content = getJedis().get(key);

            if (content != null) {
                return (Transaction) SerializationUtils.deserialize(content);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected List<Transaction> doFindAll() {

        try {
            List<Transaction> transactions = new ArrayList<Transaction>();
            Set<byte[]> keys = getJedis().keys((keyPrefix + "*").getBytes());

            for (byte[] key : keys) {
                byte[] content = getJedis().get(key);

                if (content != null) {
                    transactions.add((Transaction) SerializationUtils.deserialize(content));
                }
            }

            return transactions;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
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
}
