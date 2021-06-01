package org.mengyun.tcctransaction.server.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.mengyun.tcctransaction.repository.helper.JedisCallback;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.server.S;
import org.mengyun.tcctransaction.server.constants.LuaScriptConstant;
import org.mengyun.tcctransaction.server.model.Page;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.mengyun.tcctransaction.utils.ByteUtils;
import redis.clients.jedis.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Lee on 2020/9/22 11:28.
 * tcc-transaction
 */
@Slf4j
public class RedisTransactionDao implements TransactionDao {

    private static final String DELETE_KEY_PREIFX = "DELETE:";
    private static final int DELETE_KEY_KEEP_TIME = 3 * 24 * 3600;
    private final JedisPool jedisPool;
    private final String domain;

    public RedisTransactionDao(String domain, String host, int port, int database, String password) {
        this.domain = domain;
        this.jedisPool = create(domain, host, port, password, database);
    }

    private JedisPool create(String domain, String host, int port, String password, int database) {
        JedisPool pool = new JedisPool(new GenericObjectPoolConfig<>(), host, port, 10000, password, database, domain);
        try (Jedis jedis = pool.getResource()) {
            jedis.ping();
        }
        return pool;
    }

    @Override
    public void confirm(String globalTxId, String branchQualifier) {

        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier);

                Long result = (Long) jedis.eval(LuaScriptConstant.HSET_KEY2_IF_KKEY1_EXISTS.getBytes(),
                        3, key, key, "STATUS".getBytes(), ByteUtils.intToBytes(2));

                return result == 0;
            }
        });

    }

    @Override
    public void cancel(String globalTxId, String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier);

                Long result = (Long) jedis.eval(LuaScriptConstant.HSET_KEY2_IF_KKEY1_EXISTS.getBytes(),
                        3, key, key, "STATUS".getBytes(), ByteUtils.intToBytes(3));

                return result == 0;
            }
        });
    }

    @Override
    public void delete(String globalTxId, String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {
                String key = new String(RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier));
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
    public void restore(String globalTxId, String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {
                String restoreKeyName = new String(RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier));
                String deleteKeyName = DELETE_KEY_PREIFX + restoreKeyName;
                Long result = jedis.renamenx(deleteKeyName, restoreKeyName);
                jedis.persist(restoreKeyName);
                return result > 0;
            }
        });
    }

    @Override
    public void resetRetryCount(String globalTxId, String branchQualifier) {
        RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier);


                Long result = (Long) jedis.eval(LuaScriptConstant.HSET_KEY2_IF_KKEY1_EXISTS.getBytes(),
                        3, key, key, "RETRIED_COUNT".getBytes(), ByteUtils.intToBytes(0));

                return result == 0;
            }
        });
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public Page<TransactionVo> findTransactions(Integer pageNum, int pageSize) {
        return findTransactionByKey(pageNum, pageSize, getDomain() + "*");
    }

    @Override
    public Page<TransactionVo> findDeletedTransactions(Integer pageNum, int pageSize) {
        return findTransactionByKey(pageNum, pageSize, DELETE_KEY_PREIFX + getDomain() + "*");
    }

    private Page<TransactionVo> findTransactionByKey(Integer pageNum, int pageSize, final String pattern) {

        int count = count(pattern);
        Page<TransactionVo> page = new Page<>();
        List<TransactionVo> content = find(pageNum, pageSize, pattern);
        page.setPages(count / pageSize + (count % pageSize > 0 ? 1 : 0));
        page.setTotal(count);
        page.setItems(content);
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);

        return page;

    }


    @Override
    public int count(String domain) {
        String pattern = domain + "*";
        return RedisHelper.execute(jedisPool, new JedisCallback<Integer>() {
            @Override
            public Integer doInJedis(Jedis jedis) {
                int count = 0;
                String cursor = RedisHelper.REDIS_SCAN_INIT_CURSOR;
                ScanParams scanParams = RedisHelper.scanArgs(pattern, RedisHelper.SCAN_COUNT);
                do {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    count += scanResult.getResult().size();
                    cursor = scanResult.getCursor();
                } while (!cursor.equals(RedisHelper.REDIS_SCAN_INIT_CURSOR));
                return count;
            }
        });
    }

    @Override
    public List<TransactionVo> find(Integer pageNum, int pageSize, String pattern) {
        return RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
            @Override
            public List<TransactionVo> doInJedis(Jedis jedis) {

                int start = (pageNum - 1) * pageSize;

                int end = pageNum * pageSize;

                ArrayList<String> allKeys = new ArrayList<>();

                String cursor = RedisHelper.REDIS_SCAN_INIT_CURSOR;

                ScanParams scanParams = RedisHelper.scanArgs(pattern, RedisHelper.SCAN_COUNT);

                do {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    allKeys.addAll(scanResult.getResult());
                    cursor = scanResult.getCursor();
                } while (!cursor.equals(RedisHelper.REDIS_SCAN_INIT_CURSOR) && allKeys.size() < end);


                if (allKeys.size() < start) {
                    return Collections.emptyList();
                }

                if (end > allKeys.size()) {
                    end = allKeys.size();
                }

                final List<String> keys = allKeys.subList(start, end);

                Pipeline pipeline = jedis.pipelined();

                for (final String key : keys) {
                    pipeline.hgetAll(key);
                }

                List<Map<String, String>> result = pipeline.syncAndReturnAll().stream()
                        .map(new Function<Object, Map<String, String>>() {
                            @Override
                            public Map<String, String> apply(Object o) {

                                return (Map<String, String>) o;
                            }
                        }).collect(Collectors.toList());

                return S.zip(keys.stream(),
                        result.stream(),
                        new BiFunction<String, Map<String, String>, TransactionVo>() {
                            @Override
                            public TransactionVo apply(String s, Map<String, String> data) {


                                TransactionVo transactionVo = new TransactionVo();
                                transactionVo.setDomain(domain);
                                transactionVo.setGlobalTxId(UUID.nameUUIDFromBytes(data.get("GLOBAL_TX_ID").getBytes()).toString());
                                transactionVo.setBranchQualifier(UUID.nameUUIDFromBytes(data.get("BRANCH_QUALIFIER").getBytes()).toString());
                                transactionVo.setStatus(data.get("STATUS"));
                                transactionVo.setTransactionType(data.get("TRANSACTION_TYPE"));
                                transactionVo.setRetriedCount(data.get("RETRIED_COUNT"));
                                transactionVo.setCreateTime(data.get("CREATE_TIME"));
                                transactionVo.setLastUpdateTime(data.get("LAST_UPDATE_TIME"));
                                transactionVo.setContentView(data.get("CONTENT_VIEW"));
                                return transactionVo;
                            }
                        }).distinct().collect(Collectors.toList());


            }
        });
    }

    @Override
    public void close() throws Exception {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
