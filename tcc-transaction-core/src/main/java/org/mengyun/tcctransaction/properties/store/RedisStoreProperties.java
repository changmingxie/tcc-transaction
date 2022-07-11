package org.mengyun.tcctransaction.properties.store;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Nervose.Wu
 * @date 2022/5/24 17:44
 */
public class RedisStoreProperties extends AbstractJedisStoreProperties {

    private String host = "127.0.0.1";
    private int port = 6379;
    private int database = 0;
    private JedisPool instance;

    public JedisPool getJedisPool() {
        if (instance == null) {
            JedisPoolConfig config = getJedisPoolConfig();
            instance = new JedisPool(config, host, port, getConnectionTimeout(), getSoTimeout(), getPassword(), database,
                    null,
                    false,
                    null,
                    null,
                    null);
        }
        return instance;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}
