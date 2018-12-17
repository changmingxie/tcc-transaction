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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.text.ParseException;
import java.util.*;

/**
 * Created by changming.xie on 9/7/16.
 */
public class RedisTransactionDao implements TransactionDao {

    private static final int DELETE_KEY_KEEP_TIME = 3 * 24 * 3600;
    private static final String DELETE_KEY_PREIFX = "DELETE:";
    private static final Logger logger = LoggerFactory.getLogger(RedisTransactionDao.class);

    private JedisPool jedisPool;

    private String keySuffix;

    private String domain;

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


    public PageDto<TransactionVo> findTransactions(Integer pageNum, int pageSize) {
        return findTransactionByKey(pageNum, pageSize, getKeyPrefix() + "*");
    }

    public PageDto<TransactionVo> findDeletedTransactions(Integer pageNum, int pageSize) {
        return findTransactionByKey(pageNum, pageSize, DELETE_KEY_PREIFX + getKeyPrefix() + "*");
    }

    private PageDto<TransactionVo> findTransactionByKey(Integer pageNum, int pageSize, final String keyPattern) {

        PageDto<TransactionVo> pageDto = new PageDto<TransactionVo>();

        pageDto.setPageNum(pageNum);
        pageDto.setPageSize(pageSize);

        int start = (pageNum - 1) * pageSize;
        int end = pageNum * pageSize;
        final int endIndex = end;

        final List<String> allKeys = new ArrayList<String>();

        RedisHelper.execute(jedisPool, new JedisCallback<Object>() {
            public Object doInJedis(Jedis jedis) {
                if (RedisHelper.isSupportScanCommand(jedis)) {
                    logger.debug("redis server support scan command.");
                    String cursor = RedisHelper.SCAN_INIT_CURSOR;
                    ScanParams scanParams = RedisHelper.buildDefaultScanParams(keyPattern, RedisHelper.SCAN_COUNT);
                    do {
                        ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                        allKeys.addAll(scanResult.getResult());
                        cursor = scanResult.getStringCursor();
                    } while (!cursor.equals(RedisHelper.SCAN_INIT_CURSOR) && allKeys.size() < endIndex);

                } else {
                    logger.debug("redis server do not support scan command. use keys instead");
                    allKeys.addAll(jedis.keys(keyPattern));
                }

                return null;
            }
        });

        if (end > allKeys.size()) {
            end = allKeys.size();
        }

        List<TransactionVo> transactionVos = null;

        if (allKeys.size() < start) {

            transactionVos = new ArrayList<TransactionVo>();

        } else {

            final List<String> keys = allKeys.subList(start, end);

            transactionVos = RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
                @Override
                public List<TransactionVo> doInJedis(Jedis jedis) {

                    try {

                        return RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
                            @Override
                            public List<TransactionVo> doInJedis(Jedis jedis) {

                                Pipeline pipeline = jedis.pipelined();

                                for (final String key : keys) {
                                    pipeline.hgetAll(key.getBytes());
                                }

                                return buildTransitionVos(pipeline.syncAndReturnAll());
                            }
                        });

                    } catch (Exception e) {
                        throw new TransactionIOException(e);
                    }

                }
            });
        }

        pageDto.setData(transactionVos);
        pageDto.setTotalCount(allKeys.size());

        return pageDto;
    }

    private List<TransactionVo> buildTransitionVos(List<Object> result) {
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
                if (propertyMap.get("GLOBAL_TX_ID") != null) {
                    transactionVo.setGlobalTxId(UUID.nameUUIDFromBytes(propertyMap.get("GLOBAL_TX_ID")).toString());
                } else {
                    continue;
                }
                if (propertyMap.get("BRANCH_QUALIFIER") != null) {
                    transactionVo.setBranchQualifier(UUID.nameUUIDFromBytes(propertyMap.get("BRANCH_QUALIFIER")).toString());
                } else {
                    continue;
                }
                if (propertyMap.get("STATUS") != null) {
                    transactionVo.setStatus(ByteUtils.bytesToInt(propertyMap.get("STATUS")));
                }
                if (propertyMap.get("TRANSACTION_TYPE") != null) {
                    transactionVo.setTransactionType(ByteUtils.bytesToInt(propertyMap.get("TRANSACTION_TYPE")));
                }
                if (propertyMap.get("RETRIED_COUNT") != null) {
                    transactionVo.setRetriedCount(ByteUtils.bytesToInt(propertyMap.get("RETRIED_COUNT")));
                }
                if (propertyMap.get("CREATE_TIME") != null) {
                    transactionVo.setCreateTime(DateUtils
                            .parseDate(new String(propertyMap.get("CREATE_TIME")), "yyyy-MM-dd HH:mm:ss"));
                }
                if (propertyMap.get("LAST_UPDATE_TIME") != null) {
                    transactionVo.setLastUpdateTime(DateUtils
                            .parseDate(new String(propertyMap.get("LAST_UPDATE_TIME")), "yyyy-MM-dd HH:mm:ss"));
                }
                if (propertyMap.get("CONTENT_VIEW") != null) {
                    transactionVo.setContentView(new String(propertyMap.get("CONTENT_VIEW")));
                    transactionVo.parser();
                }
                list.add(transactionVo);

            } catch (ParseException e) {
                throw new SystemException(e);
            }
        }

        return list;
    }

    private String getKeyPrefix() {
        return keySuffix + ":";
    }

}
