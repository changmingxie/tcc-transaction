package org.mengyun.tcctransaction.server.dao;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.mengyun.tcctransaction.repository.helper.JedisCallback;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.server.model.Page;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.mengyun.tcctransaction.utils.ByteUtils;
import redis.clients.jedis.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

/**
 * Created by Lee on 2020/10/29 12:16.
 * aggregate-framework
 */

@Slf4j
public class RedisClusterTransactionDao implements TransactionDao {


    private static final String DELETE_KEY_PREIFX = "DELETE:";
    private static final int DELETE_KEY_KEEP_TIME = 3 * 24 * 3600;
    public static String SCRIPT = "if redis.call(\"exists\",KEYS[1])==1 then\n" +
            "\treturn redis.call(\"hset\",KEYS[1],'STATUS',ARGV[1])\n" +
            "else\n" +
            "\treturn 0\n" +
            "end";
    public static String SCRIPT1 = "if redis.call(\"exists\",KEYS[1])==1 then\n" +
            "\treturn redis.call(\"hset\",KEYS[1],'RETRIED_COUNT',ARGV[1])\n" +
            "else\n" +
            "\treturn 0\n" +
            "end";
    private final JedisCluster cluster;
    private final JedisPool pool;
    private final String domain;

    public RedisClusterTransactionDao(Supplier<JedisCluster> cluster,
                                      Supplier<JedisPool> pool,
                                      String domain) {
        this.pool = pool.get();
        this.domain = domain;
        this.cluster = cluster.get();

    }


    @Override
    public void confirm(String globalTxId, String branchQualifier) {

        byte[] key = RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier);

        cluster.eval(SCRIPT.getBytes(), Lists.newArrayList(key), Lists.newArrayList(ByteUtils.intToBytes(2)));

    }

    @Override
    public void cancel(String globalTxId, String branchQualifier) {
        byte[] key = RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier);

        cluster.eval(SCRIPT.getBytes(), Lists.newArrayList(key), Lists.newArrayList(ByteUtils.intToBytes(3)));
    }

    @Override
    public void delete(String globalTxId, String branchQualifier) {
        String key = new String(RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier));
        String delKeyName = DELETE_KEY_PREIFX + "{" + key + "}";
        if (cluster.del(delKeyName) > 0) {
            return;
        }
        Long result = cluster.renamenx(key, delKeyName);
        cluster.expire(delKeyName, DELETE_KEY_KEEP_TIME);
    }

    @Override
    public void restore(String globalTxId, String branchQualifier) {
        String restoreKeyName = new String(RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier));
        String deleteKeyName = DELETE_KEY_PREIFX + "{" + restoreKeyName + "}";
        Long result = cluster.renamenx(deleteKeyName, restoreKeyName);
        cluster.persist(restoreKeyName);
    }

    @Override
    public void resetRetryCount(String globalTxId, String branchQualifier) {
        byte[] key = RedisHelper.getRedisKey(getDomain(), globalTxId, branchQualifier);
        cluster.eval(SCRIPT1.getBytes(), Lists.newArrayList(key), Lists.newArrayList(ByteUtils.intToBytes(0)));
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
        return findTransactionByKey(pageNum, pageSize, DELETE_KEY_PREIFX + "{" + getDomain() + "*");
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
    public int count(String pattern) {
        return RedisHelper.execute(pool, new JedisCallback<Integer>() {
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

        List<String> keys = RedisHelper.execute(pool, new JedisCallback<List<String>>() {
            @Override
            public List<String> doInJedis(Jedis jedis) {
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

                return allKeys.subList(start, end);
            }
        });

        List<TransactionVo> result = new ArrayList<>();

        for (String k : keys) {

            Map<byte[], byte[]> data = cluster.hgetAll(k.getBytes());

            if (data != null && !data.isEmpty()) {

                TransactionVo transactionVo = new TransactionVo();
                transactionVo.setDomain(domain);

                transactionVo.setGlobalTxId(UUID.nameUUIDFromBytes(data.get("GLOBAL_TX_ID".getBytes())).toString());
                transactionVo.setBranchQualifier(UUID.nameUUIDFromBytes(data.get("BRANCH_QUALIFIER".getBytes())).toString());
                transactionVo.setStatus(new String(data.get("STATUS".getBytes()), StandardCharsets.UTF_8));
                transactionVo.setTransactionType(new String(data.get("TRANSACTION_TYPE".getBytes()), StandardCharsets.UTF_8));
                transactionVo.setRetriedCount(new String(data.get("RETRIED_COUNT".getBytes()), StandardCharsets.UTF_8));
                transactionVo.setCreateTime(new String(data.get("CREATE_TIME".getBytes()), StandardCharsets.UTF_8));
                transactionVo.setLastUpdateTime(new String(data.get("LAST_UPDATE_TIME".getBytes()), StandardCharsets.UTF_8));
                transactionVo.setContentView(new String(data.get("CONTENT_VIEW".getBytes()), StandardCharsets.UTF_8));

                result.add(transactionVo);
            }
        }

        return result;
    }


    public JedisCluster getCluster() {
        return cluster;
    }

    @Override
    public void close() throws Exception {
        if (cluster != null) {
            cluster.close();
        }
        if (pool != null) {
            pool.close();
        }

    }
}
