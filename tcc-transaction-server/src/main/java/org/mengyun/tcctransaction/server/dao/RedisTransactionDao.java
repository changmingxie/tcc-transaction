package org.mengyun.tcctransaction.server.dao;

import org.apache.commons.lang3.time.DateUtils;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.repository.RedisTransactionRepository;
import org.mengyun.tcctransaction.repository.TransactionIOException;
import org.mengyun.tcctransaction.repository.helper.JedisCallback;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.server.constants.LuaScriptConstant;
import org.mengyun.tcctransaction.server.dto.PageDto;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.mengyun.tcctransaction.utils.ByteUtils;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.net.URI;
import java.net.URISyntaxException;
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


    @Override
    public List<TransactionVo> findTransactions(final Integer pageNum, final int pageSize) {

        return RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
            @Override
            public List<TransactionVo> doInJedis(Jedis jedis) {

                int start = (pageNum - 1) * pageSize;
                int end = pageNum * pageSize;

                List<String> allKeys = new ArrayList<String>();

                String pattern = getKeyPrefix() + "*";

                if (RedisHelper.isSupportScanCommand(jedis)) {
                    logger.debug("redis server support scan command.");
                    String cursor = RedisHelper.SCAN_INIT_CURSOR;
                    ScanParams scanParams = RedisHelper.buildDefaultScanParams(pattern, RedisHelper.SCAN_COUNT);
                    do {
                        ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                        allKeys.addAll(scanResult.getResult());
                        cursor = scanResult.getStringCursor();
                    } while (!cursor.equals(RedisHelper.SCAN_INIT_CURSOR) && allKeys.size() < end);

                } else {
                    logger.debug("redis server do not support scan command. use keys instead");
                    allKeys.addAll(jedis.keys(pattern));
                }


                if (allKeys.size() < start) {
                    return Collections.emptyList();
                }

                if (end > allKeys.size()) {
                    end = allKeys.size();
                }

                final List<String> keys = allKeys.subList(start, end);

                return getTransactionVoFromRedis(keys);

            }
        });
    }

    @Override
    public Integer countOfFindTransactions() {

        return RedisHelper.execute(jedisPool, new JedisCallback<Integer>() {
            @Override
            public Integer doInJedis(Jedis jedis) {

                String pattern = getKeyPrefix() + "*";
                if (RedisHelper.isSupportScanCommand(jedis)) {
                    int count = 0;
                    String cursor = RedisHelper.SCAN_INIT_CURSOR;
                    ScanParams scanParams = RedisHelper.buildDefaultScanParams(pattern, RedisHelper.SCAN_COUNT);
                    do {
                        ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                        count += scanResult.getResult().size();
                        cursor = scanResult.getStringCursor();
                    } while (!cursor.equals(RedisHelper.SCAN_INIT_CURSOR));
                    return count;
                } else {
                    return jedis.keys(pattern.getBytes()).size();
                }
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

    @Override
    public PageDto<TransactionVo> findTransactionPageDto(Integer pageNum, int pageSize) {

        PageDto<TransactionVo> pageDto = new PageDto<TransactionVo>();

        pageDto.setPageNum(pageNum);
        pageDto.setPageSize(pageSize);

        int start = (pageNum - 1) * pageSize;
        int end = pageNum * pageSize;
        final int endIndex = end;
        final List<String> allKeys = new ArrayList<String>();
        final String pattern = getKeyPrefix() + "*";

        Integer totalCount = RedisHelper.execute(jedisPool, new JedisCallback<Integer>() {
            @Override
            public Integer doInJedis(Jedis jedis) {
                if (RedisHelper.isSupportScanCommand(jedis)) {
                    logger.info("redis server support scan command.");
                    String cursor = RedisHelper.SCAN_INIT_CURSOR;
                    ScanParams scanParams = RedisHelper.buildDefaultScanParams(pattern, RedisHelper.SCAN_COUNT);

                    do {
                        ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                        allKeys.addAll(scanResult.getResult());
                        cursor = scanResult.getStringCursor();
                    } while (!cursor.equals(RedisHelper.SCAN_INIT_CURSOR) && allKeys.size() < endIndex);

                    if (!cursor.equals(RedisHelper.SCAN_INIT_CURSOR)) {
                        return allKeys.size() + 1; // 后面还有数据, 加一
                    } else {
                        return allKeys.size();
                    }

                } else {
                    logger.info("redis server do not support scan command.use keys instead");
                    allKeys.addAll(jedis.keys(pattern));
                    return allKeys.size();
                }
            }
        });

        pageDto.setTotalCount(totalCount);

        if (allKeys.size() < start) {
            pageDto.setData(new ArrayList<TransactionVo>());
        }

        if (end > allKeys.size()) {
            end = allKeys.size();
        }

        final List<String> keys = allKeys.subList(start, end);

        List<TransactionVo> transactionVos = RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
            @Override
            public List<TransactionVo> doInJedis(Jedis jedis) {

                return getTransactionVoFromRedis(keys);

            }
        });
        pageDto.setData(transactionVos);

        return pageDto;
    }

    private List<TransactionVo> getTransactionVoFromRedis(final List<String> keys) {
        try {

            return RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
                @Override
                public List<TransactionVo> doInJedis(Jedis jedis) {

                    Pipeline pipeline = jedis.pipelined();

                    for (final String key : keys) {
                        pipeline.hgetAll(key.getBytes());
                    }

                    List<TransactionVo> list = new ArrayList<TransactionVo>();
                    buildTransactionVoList(pipeline.syncAndReturnAll(), list);
                    return list;
                }
            });

        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    private void buildTransactionVoList(List<Object> result, List<TransactionVo> list) {
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
                }

                list.add(transactionVo);

            } catch (ParseException e) {
                throw new SystemException(e);
            }
        }
    }


    
}
