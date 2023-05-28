package org.mengyun.tcctransaction.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.mengyun.tcctransaction.remoting.exception.RemotingConnectException;
import org.mengyun.tcctransaction.utils.NetUtils;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyPooledFactory implements KeyedPooledObjectFactory<String, Channel> {

    private Bootstrap bootstrap;

    private NettyClientConfig nettyClientConfig;

    private ServerAddressLoader serverAddressLoader;

    public NettyPooledFactory(Bootstrap bootstrap, NettyClientConfig nettyClientConfig, ServerAddressLoader serverAddressLoader) {
        this.bootstrap = bootstrap;
        this.nettyClientConfig = nettyClientConfig;
        this.serverAddressLoader = serverAddressLoader;
    }

    @Override
    public PooledObject<Channel> makeObject(String key) throws Exception {
        InetSocketAddress socketAddress = NetUtils.toInetSocketAddress(key);
        Channel channel = null;
        ChannelFuture channelFuture = this.bootstrap.connect(socketAddress);
        try {
            channelFuture.await(this.nettyClientConfig.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS);
            if (channelFuture.isCancelled()) {
                throw new RemotingConnectException(NetUtils.parseSocketAddress(socketAddress), channelFuture.cause());
            }
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
            } else {
                throw new RemotingConnectException(NetUtils.parseSocketAddress(socketAddress), channelFuture.cause());
            }
        } catch (Exception e) {
            throw new RemotingConnectException(NetUtils.parseSocketAddress(socketAddress), e);
        }
        return new DefaultPooledObject<>(channel);
    }

    @Override
    public void destroyObject(String key, PooledObject<Channel> p) throws Exception {
        if (p.getObject() != null) {
            p.getObject().disconnect();
            p.getObject().close();
        }
    }

    @Override
    public boolean validateObject(String key, PooledObject<Channel> p) {
        return this.serverAddressLoader.isAvailableAddress(key) && p.getObject() != null && p.getObject().isActive();
    }

    @Override
    public void activateObject(String key, PooledObject<Channel> p) throws Exception {
    }

    @Override
    public void passivateObject(String key, PooledObject<Channel> p) throws Exception {
    }
}
