package org.mengyun.tcctransaction.properties.remoting;

import org.mengyun.tcctransaction.remoting.netty.NettyClientConfig;

/**
 * @author Nervose.Wu
 * @date 2022/5/24 09:46
 */
public class NettyClientProperties extends NettyProperties implements NettyClientConfig {

    private long connectTimeoutMillis = 2000L;
    private int channelPoolMaxTotal = -1;
    private int channelPoolMaxIdlePerKey = 4;
    private int channelPoolMaxTotalPerKey = 4;
    private int channelPoolMinIdlePerKey = 1;
    private long channelPoolMaxWaitMillis = 300L;
    private long channelPoolTimeBetweenEvictionRunsMillis = 10 * 1000L;
    private int numTestsPerEvictionRun = 2;
    //check the max idle time of client channel, if exceed, then send heartbeat message to keepalive the channel,
    //need < channelIdleTimeoutSeconds(60) in ServerConfig
    private int channelMaxIdleTimeSeconds = 20;
    //reconnect to the server interval
    private int reconnectIntervalSeconds = 5;

    @Override
    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(long connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    @Override
    public int getChannelPoolMaxTotal() {
        return channelPoolMaxTotal;
    }

    public void setChannelPoolMaxTotal(int channelPoolMaxTotal) {
        this.channelPoolMaxTotal = channelPoolMaxTotal;
    }

    @Override
    public int getChannelPoolMaxIdlePerKey() {
        return channelPoolMaxIdlePerKey;
    }

    public void setChannelPoolMaxIdlePerKey(int channelPoolMaxIdlePerKey) {
        this.channelPoolMaxIdlePerKey = channelPoolMaxIdlePerKey;
    }

    @Override
    public int getChannelPoolMaxTotalPerKey() {
        return channelPoolMaxTotalPerKey;
    }

    public void setChannelPoolMaxTotalPerKey(int channelPoolMaxTotalPerKey) {
        this.channelPoolMaxTotalPerKey = channelPoolMaxTotalPerKey;
    }

    @Override
    public int getChannelPoolMinIdlePerKey() {
        return channelPoolMinIdlePerKey;
    }

    public void setChannelPoolMinIdlePerKey(int channelPoolMinIdlePerKey) {
        this.channelPoolMinIdlePerKey = channelPoolMinIdlePerKey;
    }

    @Override
    public long getChannelPoolMaxWaitMillis() {
        return channelPoolMaxWaitMillis;
    }

    public void setChannelPoolMaxWaitMillis(long channelPoolMaxWaitMillis) {
        this.channelPoolMaxWaitMillis = channelPoolMaxWaitMillis;
    }

    @Override
    public long getChannelPoolTimeBetweenEvictionRunsMillis() {
        return channelPoolTimeBetweenEvictionRunsMillis;
    }

    public void setChannelPoolTimeBetweenEvictionRunsMillis(long channelPoolTimeBetweenEvictionRunsMillis) {
        this.channelPoolTimeBetweenEvictionRunsMillis = channelPoolTimeBetweenEvictionRunsMillis;
    }

    @Override
    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    @Override
    public int getChannelMaxIdleTimeSeconds() {
        return channelMaxIdleTimeSeconds;
    }

    public void setChannelMaxIdleTimeSeconds(int channelMaxIdleTimeSeconds) {
        this.channelMaxIdleTimeSeconds = channelMaxIdleTimeSeconds;
    }

    @Override
    public int getReconnectIntervalSeconds() {
        return reconnectIntervalSeconds;
    }

    public void setReconnectIntervalSeconds(int reconnectIntervalSeconds) {
        this.reconnectIntervalSeconds = reconnectIntervalSeconds;
    }
}
