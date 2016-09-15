package org.mengyun.tcctransaction.server.dao;

import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.repository.helper.JedisCallback;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.serializer.JdkSerializationSerializer;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.mengyun.tcctransaction.utils.ByteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 * Created by changming.xie on 9/7/16.
 */
@Repository("redisTransactionDao")
public class RedisTransactionDao implements TransactionDao {

    @Autowired
    private JedisPool jedisPool;

    private ObjectSerializer serializer = new JdkSerializationSerializer();

    @Value("#{redisDomainKeyPrefix}")
    private Properties domainKeyPrefix;

    @Override
    public List<TransactionVo> findTransactions(final String domain, final Integer pageNum, final int pageSize) {


        return RedisHelper.execute(jedisPool, new JedisCallback<List<TransactionVo>>() {
            @Override
            public List<TransactionVo> doInJedis(Jedis jedis) {

                int start = (pageNum - 1) * pageSize;
                int end = pageNum * pageSize;

                ArrayList<byte[]> allKeys = new ArrayList<byte[]>(jedis.keys((domainKeyPrefix.getProperty(domain) + "*").getBytes()));

                if (allKeys.size() < start) {
                    return Collections.emptyList();
                }

                if (end > allKeys.size()) {
                    end = allKeys.size();
                }

                List<byte[]> keys = allKeys.subList(start, end);

                List<TransactionVo> transactionVos = new ArrayList<TransactionVo>();

                for (byte[] key : keys) {

                    byte[] content = RedisHelper.getKeyValue(jedis, key);
                    Map<String, Object> map = (Map<String, Object>) serializer.deserialize(content);

                    TransactionVo transactionVo = new TransactionVo();
                    transactionVo.setDomain(domain);
                    transactionVo.setGlobalTxId(DatatypeConverter.printHexBinary((byte[]) map.get("GLOBAL_TX_ID")));
                    transactionVo.setBranchQualifier(DatatypeConverter.printHexBinary((byte[]) map.get("BRANCH_QUALIFIER")));
                    transactionVo.setStatus((Integer) map.get("STATUS"));
                    transactionVo.setTransactionType((Integer) map.get("TRANSACTION_TYPE"));
                    transactionVo.setRetriedCount((Integer) map.get("RETRIED_COUNT"));
                    transactionVo.setCreateTime((Date) map.get("CREATE_TIME"));
                    transactionVo.setLastUpdateTime((Date) map.get("LAST_UPDATE_TIME"));
                    transactionVos.add(transactionVo);
                }
                return transactionVos;
            }
        });
    }

    @Override
    public Integer countOfFindTransactions(String domain) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.keys((domainKeyPrefix.getProperty(domain) + "*").getBytes()).size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public boolean resetRetryCount(final String domain, final byte[] globalTxId, final byte[] branchQualifier) {

        if (domainKeyPrefix.getProperty(domain) == null) {
            return false;
        }

        return RedisHelper.execute(jedisPool, new JedisCallback<Boolean>() {
            @Override
            public Boolean doInJedis(Jedis jedis) {

                byte[] key = RedisHelper.getRedisKey(domainKeyPrefix.getProperty(domain), new TransactionXid(globalTxId, branchQualifier));
                byte[] content = RedisHelper.getKeyValue(jedis, key);

                Map<String, Object> map = (Map<String, Object>) serializer.deserialize(content);

                map.put("RETRIED_COUNT", 0);
                map.put("LAST_UPDATE_TIME", new Date());
                map.put("VERSION", ((Long) map.get("VERSION")) + 1);

                Long result = jedis.hsetnx(key, ByteUtils.longToBytes((Long) map.get("VERSION")), serializer.serialize(map));

                return result > 0;
            }
        });
    }
}
