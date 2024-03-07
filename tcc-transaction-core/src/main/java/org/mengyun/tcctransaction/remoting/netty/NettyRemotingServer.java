package org.mengyun.tcctransaction.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.monitor.ServerFlowMonitor;
import org.mengyun.tcctransaction.remoting.RemotingServer;
import org.mengyun.tcctransaction.remoting.RequestProcessor;
import org.mengyun.tcctransaction.remoting.codec.NettyDecoder;
import org.mengyun.tcctransaction.remoting.codec.NettyEncoder;
import org.mengyun.tcctransaction.remoting.exception.RemotingException;
import org.mengyun.tcctransaction.remoting.exception.RemotingSendRequestException;
import org.mengyun.tcctransaction.remoting.exception.RemotingTimeoutException;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;
import org.mengyun.tcctransaction.serializer.RemotingCommandSerializer;
import org.mengyun.tcctransaction.stats.StatsManager;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyRemotingServer extends AbstractNettyRemoting implements RemotingServer<ChannelHandlerContext> {

    private static final Logger logger = LoggerFactory.getLogger(NettyRemotingServer.class);

    private NettyServerConfig nettyServerConfig;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup bossEventLoopGroup;

    private EventLoopGroup workEventLoopGroup;

    private DefaultEventExecutorGroup eventExecutorGroup;

    private ChannelHandler[] channelHandlers;

    private RemotingCommandSerializer serializer;

    public NettyRemotingServer(RemotingCommandSerializer serializer, NettyServerConfig nettyServerConfig, StatsManager statsManager) {
        this.nettyServerConfig = nettyServerConfig;
        this.serializer = serializer;
        this.serverBootstrap = new ServerBootstrap();
        this.statsManager = statsManager;

        if (useEpoll()) {

            this.bossEventLoopGroup = new EpollEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyBossEpollLoopSelector_%d", this.threadIndex.incrementAndGet()));
                }
            });

            this.workEventLoopGroup = new EpollEventLoopGroup(nettyServerConfig.getWorkSelectorThreadSize(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int totalThreads = nettyServerConfig.getWorkSelectorThreadSize();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyWorkEpollLoopSelector_%d_%d", totalThreads, threadIndex.incrementAndGet()));
                }
            });


        } else {
            this.bossEventLoopGroup = new NioEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyBossNioLoopSelector_%d", this.threadIndex.incrementAndGet()));
                }
            });

            this.workEventLoopGroup = new NioEventLoopGroup(nettyServerConfig.getWorkSelectorThreadSize(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int totalThreads = nettyServerConfig.getWorkSelectorThreadSize();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyWorkNioLoopSelector_%d_%d", totalThreads, threadIndex.incrementAndGet()));
                }
            });
        }

        this.eventExecutorGroup = new DefaultEventExecutorGroup(
                nettyServerConfig.getWorkerThreadSize(),
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("NettyServerWorkThread_%d", this.threadIndex.incrementAndGet()));
                    }
                }
        );
    }

    @Override
    public void start() {

        this.serverBootstrap.group(this.bossEventLoopGroup, this.workEventLoopGroup)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, nettyServerConfig.getSocketBacklog())
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getSocketRcvBufSize())
                .childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getSocketSndBufSize())
                .localAddress(new InetSocketAddress(this.nettyServerConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                eventExecutorGroup,
                                new LoggingHandler(LogLevel.DEBUG),
                                new LengthFieldBasedFrameDecoder(nettyServerConfig.getFrameMaxLength(),
                                        0, 4, 0,
                                        4),
                                new LengthFieldPrepender(4),
                                new NettyDecoder(serializer),
                                new NettyEncoder(serializer),
                                new ReadTimeoutHandler(nettyServerConfig.getChannelIdleTimeoutSeconds()),
                                new HeartBeatRespHandler(),
                                new NettyServerMetricsHandler(statsManager),
                                new NettyServerHandler()
                        );

                        if (channelHandlers != null) {
                            ch.pipeline().addLast(channelHandlers);
                        }
                    }
                });

        try {
            ChannelFuture channelFuture = this.serverBootstrap.bind().sync();
        } catch (InterruptedException e) {
            throw new SystemException("this.serverBootstrap.bind().sync() InterruptedException", e);
        }

        logger.info("NettyRemotingServer started!");
    }

    @Override
    public void shutdown() {
        this.bossEventLoopGroup.shutdownGracefully().syncUninterruptibly();
        this.workEventLoopGroup.shutdownGracefully().syncUninterruptibly();
        this.eventExecutorGroup.shutdownGracefully();

        this.serverBootstrap = null;
    }

    @Override
    public void registerDefaultProcessor(RequestProcessor<ChannelHandlerContext> processor, ExecutorService executor) {
        this.defaultRequestProcessor = new ImmutablePair<>(processor, executor);
    }

    @Override
    public void registerProcessor(int requestCode, RequestProcessor<ChannelHandlerContext> processor, ExecutorService executor) {
        this.processorTable.put(requestCode, new ImmutablePair<>(processor, executor));
    }

    @Override
    public void registerChannelHandlers(ChannelHandler... channelHandlers) {
        this.channelHandlers = channelHandlers;
    }

    @Override
    public RemotingCommand invokeSync(String key, RemotingCommand request, long timeoutMillis) {

        int requestId = request.getRequestId();

        Channel channel = FactoryBuilder.factoryOf(ChannelGroupMap.class).getInstance().getChannel(key);

        if (channel == null) {
            throw new RemotingException("channel not exist for key:" + key);
        }

        SocketAddress socketAddress = channel.remoteAddress();

        try {

            ResponseFuture responseFuture = new ResponseFuture();

            this.responseTable.put(requestId, responseFuture);

            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
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

    @ChannelHandler.Sharable
    class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand cmd) throws Exception {
            ServerFlowMonitor.count();
            cmd.setReceiveTime(System.currentTimeMillis());
            processMessageReceived(ctx, cmd);
        }
    }

    @ChannelHandler.Sharable
    class NettyServerMetricsHandler extends ChannelOutboundHandlerAdapter {
        private StatsManager statsManage;

        public NettyServerMetricsHandler(StatsManager statsManage) {
            this.statsManage = statsManage;
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            try {
                if (msg instanceof RemotingCommand) {
                    RemotingCommand remotingCommand = (RemotingCommand) msg;
                    if (remotingCommand.getCode() == RemotingCommandCode.SERVICE_RESP && remotingCommand.getOriginalCommand() != null) {
                        RemotingCommand originCommand = remotingCommand.getOriginalCommand();
                        statsManage.incRpcRequestCost(originCommand.getServiceCode(), System.currentTimeMillis() - originCommand.getReceiveTime());
                        if (RemotingCommandCode.isFail(remotingCommand.getCode())) {
                            statsManage.incFailRpcRequestNum(originCommand.getServiceCode());
                        } else {
                            statsManage.incSuccessRpcRequestNum(originCommand.getServiceCode());
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("failed to record stats data", e);
            }
            super.write(ctx, msg, promise);
        }
    }
}
