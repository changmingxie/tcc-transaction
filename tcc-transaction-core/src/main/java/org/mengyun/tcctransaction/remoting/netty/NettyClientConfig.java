package org.mengyun.tcctransaction.remoting.netty;

public interface NettyClientConfig extends NettyConfig {

    long getConnectTimeoutMillis();

    int getChannelPoolMaxTotal();

    int getChannelPoolMaxIdlePerKey();

    int getChannelPoolMaxTotalPerKey();

    int getChannelPoolMinIdlePerKey();

    long getChannelPoolMaxWaitMillis();

    long getChannelPoolTimeBetweenEvictionRunsMillis();

    long getChannelPoolSoftMinEvictableIdleTimeMillis();

    int getNumTestsPerEvictionRun();

    int getChannelMaxIdleTimeSeconds();

    int getReconnectIntervalSeconds();
}
