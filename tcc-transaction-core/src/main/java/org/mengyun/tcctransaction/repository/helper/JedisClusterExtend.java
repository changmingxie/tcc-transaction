package org.mengyun.tcctransaction.repository.helper;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.utils.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * 加载JedisCluster
 *
 * @author zc.ding
 * @create 2018/6/13
 */
public class JedisClusterExtend {
    
    static final Logger logger = Logger.getLogger(JedisClusterExtend.class.getSimpleName());
    
    private String redisClusterIp;
    private GenericObjectPoolConfig jedisPoolConfig;
    private int connectionTimeout = 0;
    private int soTimeout = 0;
    private int maxAttempts = 0;
    private String password;

    private JedisCluster jedisCluster;

    public JedisClusterExtend(String redisClusterIp){
        this(redisClusterIp, new GenericObjectPoolConfig());
    }

    public JedisClusterExtend(String redisClusterIp, final GenericObjectPoolConfig genericObjectPoolConfig){
        validIp(redisClusterIp, genericObjectPoolConfig);
        jedisCluster = new JedisCluster(getHostAndPort(), jedisPoolConfig);
    }

    public JedisClusterExtend(String redisClusterIp, int connectionTimeout, int soTimeout, int maxAttempts, String password, final GenericObjectPoolConfig genericObjectPoolConfig){
        validIp(redisClusterIp, genericObjectPoolConfig);
        jedisCluster = new JedisCluster(getHostAndPort(), connectionTimeout, soTimeout, maxAttempts, password, jedisPoolConfig);
    }
    
    /**
    *  获取JedisCluster
    *  @Method_Name             ：getJedisCluster
    * 
    *  @return redis.clients.jedis.JedisCluster
    *  @Creation Date           ：2018/6/13
    *  @Author                  ：zc.ding@foxmail.com
    */
    public JedisCluster getJedisCluster(){
        return jedisCluster;
    }
    
    /**
    *  获得redis集群地址
    *  @Method_Name             ：getHostAndPort
    * 
    *  @return java.util.Set<redis.clients.jedis.HostAndPort>
    *  @Creation Date           ：2018/7/11
    *  @Author                  ：zc.ding@foxmail.com
    */
    public Set<HostAndPort> getHostAndPort(){
        Set<HostAndPort> set = new HashSet<HostAndPort>();
        String[] arr = redisClusterIp.split(",");
        for(String host : arr){
            String[] ipPort = host.trim().split(":");
            if(ipPort.length < 2){
                throw new RuntimeException(ipPort + " is Invalid.");
            }
            set.add(new HostAndPort(ipPort[0], Integer.parseInt(ipPort[1])));
        }
        return set;
    }
    
    /**
    *  验证地址及poolConfig准确性
    *  @Method_Name             ：validIp
    *  @param redisClusterIp
    *  @param genericObjectPoolConfig
    *  @return void
    *  @Creation Date           ：2018/7/11
    *  @Author                  ：zc.ding@foxmail.com
    */
    public void validIp(String redisClusterIp, final GenericObjectPoolConfig genericObjectPoolConfig){
        logger.info("redis cluster address is " + redisClusterIp);
        if(!StringUtils.isNotEmpty(redisClusterIp)){
            throw new RuntimeException("can't find redis cluster ip.");
        }
        if(genericObjectPoolConfig == null){
            throw new RuntimeException("JectPoolConfig not defined.");
        }
        this.redisClusterIp = redisClusterIp;
        this.jedisPoolConfig = genericObjectPoolConfig;
    }
    
}
