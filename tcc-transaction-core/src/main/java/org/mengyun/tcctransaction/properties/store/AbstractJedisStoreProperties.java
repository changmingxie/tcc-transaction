package org.mengyun.tcctransaction.properties.store;

import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Nervose.Wu
 * @date 2022/5/24 19:21
 */
public abstract class AbstractJedisStoreProperties {

    private String password;

    private int soTimeout = 300;

    private int connectionTimeout = 1000;

    private JedisPoolConfig poolConfig = new JedisPoolConfig();

    protected JedisPoolConfig getJedisPoolConfig() {
        return poolConfig;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public JedisPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(JedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }
}
