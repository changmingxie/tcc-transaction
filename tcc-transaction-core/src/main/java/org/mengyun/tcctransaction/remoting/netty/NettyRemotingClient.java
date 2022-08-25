package org.mengyun.tcctransaction.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.remoting.RemotingClient;
import org.mengyun.tcctransaction.remoting.RequestProcessor;
import org.mengyun.tcctransaction.remoting.codec.NettyDecoder;
import org.mengyun.tcctransaction.remoting.codec.NettyEncoder;
import org.mengyun.tcctransaction.remoting.exception.RemotingSendRequestException;
import org.mengyun.tcctransaction.remoting.exception.RemotingTimeoutException;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.serializer.RemotingCommandSerializer;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyRemotingClient extends AbstractNettyRemoting implements RemotingClient<ChannelHandlerContext> {

    private static final Logger logger = LoggerFactory.getLogger(NettyRemotingClient.class);

    private NettyClientConfig nettyClientConfig;

    private EventLoopGroup workEventLoopGroup;

    private Bootstrap bootstrap = new Bootstrap();

    private DefaultEventExecutorGroup eventExecutorGroup;

    private GenericKeyedObjectPool<String, Channel> nettyClientKeyPool;

    private RemotingCommandSerializer serializer;

    private ChannelHandler[] channelHandlers;

    private ServerAddressLoader serverAddressLoader;

    public NettyRemotingClient(RemotingCommandSerializer serializer, NettyClientConfig nettyClientConfig, ServerAddressLoader serverAddressLoader) {

        this.nettyClientConfig = nettyClientConfig;

        this.serializer = serializer;

        this.serverAddressLoader = serverAddressLoader;

        this.nettyClientKeyPool = new GenericKeyedObjectPool<>(new NettyPooledFactory(this.bootstrap, nettyClientConfig, this.serverAddressLoader), getChannelPoolConfig(nettyClientConfig));

        this.eventExecutorGroup = new DefaultEventExecutorGroup(nettyClientConfig.getWorkerThreadSize(),
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("NettyClientWorkderThread_%d", this.threadIndex.incrementAndGet()));
                    }
                });

        if (useEpoll()) {
            this.workEventLoopGroup = new EpollEventLoopGroup(nettyClientConfig.getWorkSelectorThreadSize(),
                    new ThreadFactory() {
                        private AtomicInteger threadIndex = new AtomicInteger(0);

                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, String.format("NettyClientEpollLoopSelector_%d", this.threadIndex.incrementAndGet()));
                        }
                    }
            );
        } else {
            this.workEventLoopGroup = new NioEventLoopGroup(nettyClientConfig.getWorkSelectorThreadSize(),
                    new ThreadFactory() {
                        private AtomicInteger threadIndex = new AtomicInteger(0);

                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, String.format("NettyClientNioLoopSelector_%d", this.threadIndex.incrementAndGet()));
                        }
                    }
            );
        }
    }

    @Override
    public void start() {

        this.bootstrap.group(this.workEventLoopGroup)
                .channel(useEpoll() ? EpollSocketChannel.class : NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Long.valueOf(nettyClientConfig.getConnectTimeoutMillis()).intValue())
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                //TODO: need add more handlers
                                pipeline.addLast(
                                        eventExecutorGroup,
                                        new LoggingHandler(LogLevel.DEBUG),
                                        new IdleStateHandler(0, nettyClientConfig.getChannelMaxIdleTimeSeconds(), 0),
                                        new LengthFieldBasedFrameDecoder(nettyClientConfig.getFrameMaxLength(),
                                                0, 4, 0,
                                                4),
                                        new LengthFieldPrepender(4),
                                        new NettyEncoder(serializer),
                                        new NettyDecoder(serializer),
                                        new HeartBeatReqHandler(),
                                        new ProcessReadHandler()
                                );

                                if (channelHandlers != null) {
                                    pipeline.addLast(channelHandlers);
                                }
                            }
                        }
                );

        if (Epoll.isAvailable()) {
            this.bootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED)
                    .option(EpollChannelOption.TCP_QUICKACK, true);
        }

        if (nettyClientConfig.getSocketSndBufSize() > 0) {
            this.bootstrap.option(ChannelOption.SO_SNDBUF, nettyClientConfig.getSocketSndBufSize());
        }

        if (nettyClientConfig.getSocketRcvBufSize() > 0) {
            this.bootstrap.option(ChannelOption.SO_RCVBUF, nettyClientConfig.getSocketRcvBufSize());
        }
    }

    @Override
    public void shutdown() {

        logger.info("shutdown netty remoting client, this may take some seconds ...");

        this.workEventLoopGroup.shutdownGracefully().syncUninterruptibly();

        this.eventExecutorGroup.shutdownGracefully().syncUninterruptibly();

        this.nettyClientKeyPool.close();
        logger.info("shutdown netty remoting client done.");
    }

    @Override
    public void registerDefaultProcessor(RequestProcessor<ChannelHandlerContext> processor, ExecutorService executor) {
        this.defaultRequestProcessor = new ImmutablePair<>(processor, executor);
    }

    @Override
    public void registerChannelHandlers(ChannelHandler[] channelHandlers) {
        this.channelHandlers = channelHandlers;
    }

    @Override
    public RemotingCommand invokeSync(RemotingCommand request, long timeoutMillis) {
        return invokeSync(null, request, timeoutMillis);
    }

    @Override
    public RemotingCommand invokeSync(final String address, final RemotingCommand request, final long timeoutMillis) {

        int requestId = request.getRequestId();

        long beginStartTime = System.currentTimeMillis();

        Pair<String, Channel> pair = borrowAvailableChannelFromPool(address);

        String selectedAddress = pair.getKey();
        Channel selectedChannel = pair.getValue();
        SocketAddress socketAddress = selectedChannel.remoteAddress();
        try {

            ResponseFuture responseFuture = new ResponseFuture();
            this.responseTable.put(requestId, responseFuture);

            try {
                long costTime = System.currentTimeMillis() - beginStartTime;

                if (timeoutMillis < costTime) {
                    throw new RemotingTimeoutException(NetUtils.parseSocketAddress(socketAddress));
                }

                selectedChannel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            responseFuture.setSendRequestSuccess(true);
                        } else {
                            responseFuture.setCause(channelFuture.cause());
                            responseFuture.setResponse(null);
                        }
                    }
                });

            } finally {
                returenChannelToPool(selectedAddress, selectedChannel);
            }

            RemotingCommand responseCommand = null;
            try {
                responseCommand = responseFuture.get(timeoutMillis);
            } catch (InterruptedException e) {
                throw new RemotingTimeoutException(NetUtils.parseSocketAddress(socketAddress), timeoutMillis, e);
            }

            if (null == responseCommand) {
                if (responseFuture.isSendRequestSuccess()) {
                    throw new RemotingTimeoutException(NetUtils.parseSocketAddress(socketAddress), timeoutMillis, responseFuture.getCause());
                } else {
                    throw new RemotingSendRequestException(NetUtils.parseSocketAddress(socketAddress), responseFuture.getCause());
                }
            }

            return responseCommand;
        } finally {
            this.responseTable.remove(requestId);
        }
    }

    @Override
    public void invokeOneway(RemotingCommand request, long timeoutMillis) {
        invokeOneway(null, request, timeoutMillis);
    }

    @Override
    public void invokeOneway(String address, RemotingCommand request, long timeoutMillis) {

        long beginStartTime = System.currentTimeMillis();

        Pair<String, Channel> pair = borrowAvailableChannelFromPool(address);
        String selectedAddress = pair.getKey();
        Channel selectedChannel = pair.getValue();

        SocketAddress socketAddress = selectedChannel.remoteAddress();
        try {
            long costTime = System.currentTimeMillis() - beginStartTime;

            if (timeoutMillis < costTime) {
                throw new RemotingTimeoutException(NetUtils.parseSocketAddress(socketAddress));
            }

            selectedChannel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        logger.warn("send a request command to channel <{}> failed.",NetUtils.parseSocketAddress(socketAddress));
                    }
                }
            });

        } finally {
            returenChannelToPool(selectedAddress, selectedChannel);
        }
    }

    private Pair<String, Channel> borrowAvailableChannelFromPool(String address) {

        String selectedAddress;
        if (StringUtils.isNotEmpty(address)) {
            selectedAddress = address;
        } else {
            //select one available ipAndPort, for invalid ipAndPort, nettyClientPool will invalid it
            selectedAddress = this.serverAddressLoader.selectOneAvailableAddress();

            if (StringUtils.isBlank(selectedAddress)) {
                throw new SystemException("no available servers found");
            }
        }

        Channel channel;
        try {
            channel = nettyClientKeyPool.borrowObject(selectedAddress);
            //need consider the server side is offline, and cannot connect
            //
        } catch (Exception e) {
            throw new SystemException("borrow channel from pool failed", e);
        }
        return new ImmutablePair<>(selectedAddress, channel);
    }

    private void returenChannelToPool(String address, Channel channel) {
        if (channel != null) {
            nettyClientKeyPool.returnObject(address, channel);
        }
    }

    private GenericKeyedObjectPoolConfig<Channel> getChannelPoolConfig(NettyClientConfig nettyClientConfig) {

        GenericKeyedObjectPoolConfig<Channel> config = new GenericKeyedObjectPoolConfig<>();
        config.setTestOnReturn(true);
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);

        config.setMaxTotal(nettyClientConfig.getChannelPoolMaxTotal());
        config.setMaxTotalPerKey(nettyClientConfig.getChannelPoolMaxTotalPerKey());
        config.setMaxIdlePerKey(nettyClientConfig.getChannelPoolMaxIdlePerKey());
        config.setMinIdlePerKey(nettyClientConfig.getChannelPoolMinIdlePerKey());
        config.setTimeBetweenEvictionRunsMillis(nettyClientConfig.getChannelPoolTimeBetweenEvictionRunsMillis());
        config.setNumTestsPerEvictionRun(nettyClientConfig.getNumTestsPerEvictionRun());

        config.setMaxWaitMillis(nettyClientConfig.getChannelPoolMaxWaitMillis());
        config.setBlockWhenExhausted(true);


        return config;
    }

    class ProcessReadHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand cmd) throws Exception {
            processMessageReceived(ctx, cmd);
        }
    }


}