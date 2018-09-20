package org.mengyun.tcctransaction.server.dao;

import org.apache.commons.lang3.time.DateUtils;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.repository.TransactionIOException;
import org.mengyun.tcctransaction.repository.helper.JedisCallback;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.server.constants.LuaScriptConstant;
import org.mengyun.tcctransaction.server.dto.PageDto;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.mengyun.tcctransaction.utils.ByteUtils;
import org.mengyun.tcctransaction.utils.RedisUtils;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.text.ParseException;
import java.util.*;

/**
 * Created by changming.xie on 9/7/16.
 */
public class RedisTransactionDao implements TransactionDao {

    private static final Logger logger = LoggerFactory.getLogger(RedisTransactionDao.class);

    private JedisPool jedisPool;

    private String keySuffix;

    private String domain;

    private String getKeyPrefix() {
        return keySuffix + ":";
    }

    public PageDto<TransactionVo> findTransactions(final Integer pageNum, final int pageSize) {

        PageDto<TransactionVo> pageDto = new PageDto<TransactionVo>();

        pageDto.setPageNum(pageNum);
        pageDto.setPageSize(pageSize);

        List<TransactionVo> data = RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {

            @Override
            public List<TransactionVo> doInJedis(Jedis jedis) {

                int start = (pageNum - 1) * pageSize;
                int end = pageNum * pageSize;

                List<String> allKeys = new ArrayList<String>();

                String pattern = getKeyPrefix() + "*";

                if (RedisUtils.isSupportScanCommand(jedis)) {
                    logger.info("redis server support scan command.");
                    String cursor = "0";
                    do {
                        ScanResult<String> scanResult = jedis.scan(cursor, new ScanParams().match(pattern).count(30));
                        allKeys.addAll(scanResult.getResult());
                        cursor = scanResult.getStringCursor();
                    } while (!cursor.equals("0") || allKeys.size() >= end);

                } else {
                    logger.info("redis server do not support scan command. use keys instead");
                    allKeys.addAll(jedis.keys(pattern));
                }


                if (allKeys.size() < start) {
                    return Collections.emptyList();
                }

                if (end > allKeys.size()) {
                    end = allKeys.size();
                }

                final List<String> keys = allKeys.subList(start, end);

                try {

                    return RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
                        @Override
                        public List<TransactionVo> doInJedis(Jedis jedis) {

                            Pipeline pipeline = jedis.pipelined();

                            for (final String key : keys) {
                                pipeline.hgetAll(key);
                            }

                            return BuildTransitionVoList(pipeline.syncAndReturnAll());
                        }
                    });

                } catch (Exception e) {
                    throw new TransactionIOException(e);
                }

            }
        });
    }

    @Override
    public Integer count() {

        return RedisHelper.execute(jedisPool, new JedisCallback<Integer>() {
            @Override
            public Integer doInJedis(Jedis jedis) {

                return jedis.keys(getKeyPrefix() + "*".getBytes()).size();
            }
        });

    }

    @Override
    public Integer countOfFindTransactionsDeleted() {
        return RedisHelper.execute(jedisPool, new JedisCallback<Integer>() {
            @Override
            public Integer doInJedis(Jedis jedis) {

                return jedis.keys(DELETE_KEY_PREIFX + getKeyPrefix() + "*".getBytes()).size();
            }
        });
    }

    @Override
    public void resetRetryCount(final String globalTxId, final String branchQualifier) {

        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);


                Long result = (Long) jedis.eval(LuaScriptConstant.HSET_KEY2_IF_KKEY1_EXISTS.getBytes(),
                        3, key, key, "RETRIED_COUNT".getBytes(), ByteUtils.intToBytes(0));

                return result == 0;
            }
        });
    }


    @Override
    public void delete(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {
                String key = new String(RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier));
                String delKeyName = DELETE_KEY_PREIFX + key;
                if (jedis.del(delKeyName) > 0) {
                    return true;
                }
                Long result = jedis.renamenx(key, delKeyName);
                jedis.expire(delKeyName, DELETE_KEY_KEEP_TIME);
                return result > 0;
            }
        });
    }

    @Override
    public void restore(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {
                String restoreKeyName = new String(RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier));
                String deleteKeyName = DELETE_KEY_PREIFX + restoreKeyName;
                Long result = jedis.renamenx(deleteKeyName, restoreKeyName);
                jedis.persist(restoreKeyName);
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

                Long result = (Long) jedis.eval(LuaScriptConstant.HSET_KEY2_IF_KKEY1_EXISTS.getBytes(),
                        3, key, key, "STATUS".getBytes(), ByteUtils.intToBytes(2));

                return result == 0;
            }
        });
    }

    @Override
    public void cancel(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);

                Long result = (Long) jedis.eval(LuaScriptConstant.HSET_KEY2_IF_KKEY1_EXISTS.getBytes(),
                        3, key, key, "STATUS".getBytes(), ByteUtils.intToBytes(3));

                return result == 0;
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
