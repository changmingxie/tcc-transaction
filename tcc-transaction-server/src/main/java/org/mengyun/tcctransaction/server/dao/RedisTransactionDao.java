package org.mengyun.tcctransaction.server.dao;

import org.apache.commons.lang3.time.DateUtils;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.repository.TransactionIOException;
import org.mengyun.tcctransaction.repository.helper.JedisCallback;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.mengyun.tcctransaction.utils.ByteUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.text.ParseException;
import java.util.*;

/**
 * Created by changming.xie on 9/7/16.
 */
public class RedisTransactionDao implements TransactionDao {

    private String KEY_NAME_SPACE = "TCC";

    private JedisPool jedisPool;

    private String keySuffix;

    private String domain;

    private String getKeyPrefix() {
        return KEY_NAME_SPACE + ":" + keySuffix + ":";
    }

    @Override
    public List<TransactionVo> findTransactions(final Integer pageNum, final int pageSize) {


        return RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
            @Override
            public List<TransactionVo> doInJedis(Jedis jedis) {

                int start = (pageNum - 1) * pageSize;
                int end = pageNum * pageSize;

                ArrayList<byte[]> allKeys = new ArrayList<byte[]>(jedis.keys((getKeyPrefix() + "*").getBytes()));

                if (allKeys.size() < start) {
                    return Collections.emptyList();
                }

                if (end > allKeys.size()) {
                    end = allKeys.size();
                }

                final List<byte[]> keys = allKeys.subList(start, end);

                try {

                    return RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
                        @Override
                        public List<TransactionVo> doInJedis(Jedis jedis) {

                            Pipeline pipeline = jedis.pipelined();

                            for (final byte[] key : keys) {
                                pipeline.hgetAll(key);
                            }
                            List<Object> result = pipeline.syncAndReturnAll();

                            List<TransactionVo> list = new ArrayList<TransactionVo>();
                            for (Object data : result) {
                                try {

                                    Map<byte[], byte[]> map1 = (Map<byte[], byte[]>) data;

                                    Map<String, byte[]> propertyMap = new HashMap<String, byte[]>();

                                    for (Map.Entry<byte[], byte[]> entry : map1.entrySet()) {
                                        propertyMap.put(new String(entry.getKey()), entry.getValue());
                                    }


                                    TransactionVo transactionVo = new TransactionVo();
                                    transactionVo.setDomain(domain);
                                    transactionVo.setGlobalTxId(UUID.nameUUIDFromBytes(propertyMap.get("GLOBAL_TX_ID")).toString());
                                    transactionVo.setBranchQualifier(UUID.nameUUIDFromBytes(propertyMap.get("BRANCH_QUALIFIER")).toString());
                                    transactionVo.setStatus(ByteUtils.bytesToInt(propertyMap.get("STATUS")));
                                    transactionVo.setTransactionType(ByteUtils.bytesToInt(propertyMap.get("TRANSACTION_TYPE")));
                                    transactionVo.setRetriedCount(ByteUtils.bytesToInt(propertyMap.get("RETRIED_COUNT")));
                                    transactionVo.setCreateTime(DateUtils.parseDate(new String(propertyMap.get("CREATE_TIME")), "yyyy-MM-dd HH:mm:ss"));
                                    transactionVo.setLastUpdateTime(DateUtils.parseDate(new String(propertyMap.get("LAST_UPDATE_TIME")), "yyyy-MM-dd HH:mm:ss"));
                                    transactionVo.setContentView(new String(propertyMap.get("CONTENT_VIEW")));
                                    list.add(transactionVo);

                                } catch (ParseException e) {
                                    throw new SystemException(e);
                                }
                            }

                            return list;
                        }
                    });

                } catch (Exception e) {
                    throw new TransactionIOException(e);
                }

            }
        });
    }

    @Override
    public Integer countOfFindTransactions() {

        return RedisHelper.execute(jedisPool, new JedisCallback<Integer>() {
            @Override
            public Integer doInJedis(Jedis jedis) {
                return jedis.keys((getKeyPrefix() + "*").getBytes()).size();
            }
        });
    }

    @Override
    public void resetRetryCount(final String globalTxId, final String branchQualifier) {

        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);
                Long result = jedis.hset(key, "RETRIED_COUNT".getBytes(), ByteUtils.intToBytes(0));
                return result > 0;
            }
        });
    }

    @Override
    public void delete(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);
                Long result = jedis.del(key);
                return result > 0;
            }
        });
    }

    @Override
    public void confirm(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);
                Long result = jedis.hset(key, "STATUS".getBytes(), ByteUtils.intToBytes(2));
                return result > 0;
            }
        });
    }

    @Override
    public void cancel(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);
                Long result = jedis.hset(key, "STATUS".getBytes(), ByteUtils.intToBytes(3));
                return result > 0;
            }
        });
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public String getKeySuffix() {
        return keySuffix;
    }

    public void setKeySuffix(String keySuffix) {
        this.keySuffix = keySuffix;
    }
}
