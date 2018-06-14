package org.mengyun.tcctransaction.server.dao;

import org.apache.commons.lang3.time.DateUtils;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.repository.TransactionIOException;
import org.mengyun.tcctransaction.repository.helper.JedisClusterCallback;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.mengyun.tcctransaction.utils.ByteUtils;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.text.ParseException;
import java.util.*;

/**
 * redis 基类
 *
 * @author zc.ding
 * @create 2018/6/14
 */
public abstract class AbstractRedisDao {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRedisDao.class);
    
    /**
    *  根据pattern加载指定条数的key
    *  @Method_Name             ：loadKeys
    *  @param jedis
    *  @param allKeys     
    *  @param total
    *  @param pattern
    *  @return void
    *  @Creation Date           ：2018/6/14
    *  @Author                  ：zc.ding@foxmail.com
    */
    private void loadKeys(Jedis jedis, List<String> allKeys, Integer total, String pattern){
        Set<String> set = new HashSet<String>();
        try {
            if (isSupportScanCommand(jedis)) {
                logger.info("redis server support scan command.");
                String cursor = "0";
                do {
                    ScanResult<String> scanResult = jedis.scan(cursor, new ScanParams().match(pattern).count(30));
                    allKeys.addAll(scanResult.getResult());
                    set.addAll(scanResult.getResult());
                    cursor = scanResult.getStringCursor();
                } while (!cursor.equals("0") || allKeys.size() >= total);
            } else {
                logger.info("redis server do not support scan command.");
                allKeys.addAll(jedis.keys(pattern));
                set.addAll(jedis.keys(pattern));
            }
        }finally {
            close(jedis);
        }
        allKeys.addAll(set);
    }
    
    /**
    *  加载keys
    *  @Method_Name             ：loadKeys
    *  @param jedisCluster
    *  @param allKeys
    *  @param total
    *  @param pattern
    *  @return void
    *  @Creation Date           ：2018/6/14
    *  @Author                  ：zc.ding@foxmail.com
    */
    public void loadKeys(JedisCluster jedisCluster, List<String> allKeys, Integer total, String pattern){
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for(String k : clusterNodes.keySet()) {
            JedisPool jp = clusterNodes.get(k);
            Jedis jedis = jp.getResource();
            loadKeys(jedis, allKeys, total, pattern);
        }
    }
    
    /**
    *  验证redis版本
    *  @Method_Name             ：isSupportScanCommand
    *  @param jedis
    *  @return boolean
    *  @Creation Date           ：2018/6/14
    *  @Author                  ：zc.ding@foxmail.com
    */
    public boolean isSupportScanCommand(Jedis jedis) {
        if (jedis == null) {
            logger.info("jedisCluster is null,");
            return false;
        }
        String serverInfo = jedis.info("Server");
        int versionIndex = serverInfo.indexOf("redis_version");
        String infoWithVersionAhead = serverInfo.substring(versionIndex);
        int versionOverIndex = infoWithVersionAhead.indexOf("\r");
        String serverVersion = infoWithVersionAhead.substring(0, versionOverIndex);
        String leastVersionForScan = "redis_version:2.8";
        if (StringUtils.isNotEmpty(serverVersion)) {
            logger.info("redis server:{}", serverVersion);
            return serverVersion.compareTo(leastVersionForScan) >= 0;
        } else {
            return false;
        }
    }
    
    /**
    *  加载TransactionVo集合
    *  @Method_Name             ：loadTransactionVos
    *  @param jedisCluster
    *  @param keys
    *  @param domain
    *  @return java.util.List<org.mengyun.tcctransaction.server.vo.TransactionVo>
    *  @Creation Date           ：2018/6/14
    *  @Author                  ：zc.ding@foxmail.com
    */
    public List<TransactionVo> loadTransactionVos(JedisCluster jedisCluster, final List<String> keys, final String domain){
        return RedisHelper.execute(jedisCluster, new JedisClusterCallback<List<TransactionVo>>() {
            @Override
            public List<TransactionVo> doInJedisCluster(JedisCluster jedisCluster) {
                try {
                    List<TransactionVo> list = new ArrayList<TransactionVo>();
                    for (final String key : keys) {
                        try{
                            Map<byte[], byte[]> map = jedisCluster.hgetAll(key.getBytes());
                            Map<String, byte[]> propertyMap = new HashMap<String, byte[]>();
                            for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
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
                        }catch(ParseException e){
                            throw new SystemException(e);
                        }
                    }
                    return list;

                } catch (Exception e) {
                    throw new TransactionIOException(e);
                }
            }
        });
    }
    
    /**
    *  @Method_Name             ：close
    *  @param jedis
    *  @return void
    *  @Creation Date           ：2018/6/14
    *  @Author                  ：zc.ding@foxmail.com
    */
    public void close(Jedis jedis){
        if(jedis != null){
            jedis.close();
        }
    }
}
