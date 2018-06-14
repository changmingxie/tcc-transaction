package org.mengyun.tcctransaction.server.dao;

import org.apache.commons.lang3.time.DateUtils;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.TransactionIOException;
import org.mengyun.tcctransaction.repository.helper.*;
import org.mengyun.tcctransaction.serializer.JdkSerializationSerializer;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;
import org.mengyun.tcctransaction.server.dto.PageDto;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.mengyun.tcctransaction.utils.ByteUtils;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.text.ParseException;
import java.util.*;

/**
*  support redis cluster
*  @Creation Date           ：2018/6/14
*  @Author                  ：zc.ding@foxmail.com
*/
public class RedisClusterTransactionDao extends AbstractRedisDao implements TransactionDao {

    private static final Logger logger = LoggerFactory.getLogger(RedisClusterTransactionDao.class);
    private String KEY_NAME_SPACE = "TCC";
    private JedisCluster jedisCluster;
    private JedisClusterExtend jedisClusterExtend;
    private String keySuffix;
    private String domain;

    private String getKeyPrefix() {
        return KEY_NAME_SPACE + ":" + keySuffix + ":";
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public void setJedisClusterExtend(JedisClusterExtend jedisClusterExtend) {
        this.jedisClusterExtend = jedisClusterExtend;
        this.jedisCluster = jedisClusterExtend.getJedisCluster();
    }

    @Override
    public List<TransactionVo> findTransactions(final Integer pageNum, final int pageSize) {
        return RedisHelper.execute(jedisCluster, new JedisClusterCallback<List<TransactionVo>>() {
            @Override
            public List<TransactionVo> doInJedisCluster(JedisCluster jedisCluster) {
                int start = (pageNum - 1) * pageSize;
                int end = pageNum * pageSize;
                List<String> allKeys = new ArrayList<String>();
                loadKeys(jedisCluster, allKeys, end, getKeyPrefix() + "*");
                if (allKeys.size() < start) {
                    return Collections.emptyList();
                }
                if (end > allKeys.size()) {
                    end = allKeys.size();
                }
                return loadTransactionVos(jedisCluster, allKeys.subList(start, end), domain);
            }
        });
    }

    @Override
    public Integer countOfFindTransactions() {
        return RedisHelper.execute(jedisCluster, new JedisClusterCallback<Integer>() {
            @Override
            public Integer doInJedisCluster(JedisCluster jedisCluster) {
                int size = 0;
                Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
                for(String k : clusterNodes.keySet()) {
                    JedisPool jp = clusterNodes.get(k);
                    Jedis jedis = jp.getResource();
                    try {
                        size += jedis.keys(getKeyPrefix() + "*".getBytes()).size();
                    }finally {
                        close(jedis);
                    }
                }
                return size;
            }
        });
    }

    @Override
    public void resetRetryCount(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisCluster, new JedisClusterCallback<Boolean>() {
            @Override
            public Boolean doInJedisCluster(JedisCluster jedisCluster) {
                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);
                return jedisCluster.hset(key, "RETRIED_COUNT".getBytes(), ByteUtils.intToBytes(0)) > 0;
            }
        });
    }

    @Override
    public void delete(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisCluster, new JedisClusterCallback<Boolean>() {
            @Override
            public Boolean doInJedisCluster(JedisCluster jedisCluster) {
                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);
                return jedisCluster.del(key) > 0;
            }
        });
    }

    @Override
    public void confirm(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisCluster, new JedisClusterCallback<Boolean>() {
            @Override
            public Boolean doInJedisCluster(JedisCluster jedisCluster) {
                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);
                Long result = jedisCluster.hset(key, "STATUS".getBytes(), ByteUtils.intToBytes(2));
                return result > 0;
            }
        });
    }

    @Override
    public void cancel(final String globalTxId, final String branchQualifier) {
        RedisHelper.execute(jedisCluster, new JedisClusterCallback<Boolean>() {
            @Override
            public Boolean doInJedisCluster(JedisCluster jedisCluster) {
                byte[] key = RedisHelper.getRedisKey(getKeyPrefix(), globalTxId, branchQualifier);
                Long result = jedisCluster.hset(key, "STATUS".getBytes(), ByteUtils.intToBytes(3));
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
        Integer totalCount = 0;
        int start = (pageNum - 1) * pageSize;
        int end = pageNum * pageSize;
        List<String> allKeys = new ArrayList<String>();
        loadKeys(jedisCluster, allKeys, end, getKeyPrefix() + "*");
        totalCount = allKeys.size();
        pageDto.setTotalCount(totalCount);
        if (allKeys.size() < start) {
            pageDto.setData(new ArrayList<TransactionVo>());
        }
        if (end > allKeys.size()) {
            end = allKeys.size();
        }
        pageDto.setData(loadTransactionVos(jedisCluster, allKeys.subList(start, end), domain));
        return pageDto;
    }
}
