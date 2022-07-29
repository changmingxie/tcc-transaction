package org.mengyun.tcctransaction;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.constants.MixAll;
import org.mengyun.tcctransaction.discovery.registry.RegistryFactory;
import org.mengyun.tcctransaction.discovery.registry.RegistryService;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.monitor.ServerFlowMonitor;
import org.mengyun.tcctransaction.processor.ServerRecoveryExecutor;
import org.mengyun.tcctransaction.processor.ServerRequestProcessor;
import org.mengyun.tcctransaction.recovery.RecoveryExecutor;
import org.mengyun.tcctransaction.recovery.RecoveryScheduler;
import org.mengyun.tcctransaction.recovery.TransactionStoreRecovery;
import org.mengyun.tcctransaction.remoting.RemotingServer;
import org.mengyun.tcctransaction.remoting.RequestProcessor;
import org.mengyun.tcctransaction.remoting.netty.ChannelGroupMap;
import org.mengyun.tcctransaction.remoting.netty.NettyRemotingServer;
import org.mengyun.tcctransaction.repository.StorageMode;
import org.mengyun.tcctransaction.serializer.RemotingCommandSerializer;
import org.mengyun.tcctransaction.serializer.TccRemotingCommandSerializer;
import org.mengyun.tcctransaction.serializer.TccTransactionStoreSerializer;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.StorageType;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.mengyun.tcctransaction.storage.TransactionStorageFactory;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.mengyun.tcctransaction.utils.StopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TccServer implements TccService {

    static final Logger logger = LoggerFactory.getLogger(TccServer.class.getSimpleName());

    private ServerConfig serverConfig = ServerConfig.DEFAULT;

    private TransactionStorage transactionStorage;

    private RecoveryExecutor recoveryExecutor;

    private RequestProcessor requestProcessor;

    private TransactionStoreRecovery transactionStoreRecovery;

    private ExecutorService requestProcessExecutor;

    private TransactionStoreSerializer transactionStoreSerializer;

    private RemotingCommandSerializer remotingCommandSerializer;

    private volatile boolean isShutdown = false;

    private RecoveryScheduler scheduler;

    private RemotingServer remotingServer;

    private RegistryService registryService;

    public TccServer(ServerConfig serverConfig) {
        if (serverConfig != null) {
            this.serverConfig = serverConfig;
        }

        this.transactionStoreSerializer = new TccTransactionStoreSerializer();
        this.remotingCommandSerializer = new TccRemotingCommandSerializer();

        if (this.serverConfig.getStorageType() == StorageType.REMOTING) {
            throw new SystemException(String.format("unsupported StorageType<%s> in server side.", this.serverConfig.getStorageType().value()));
        }

        this.remotingServer = new NettyRemotingServer(this.remotingCommandSerializer, this.serverConfig);

        this.registryService = RegistryFactory.getInstance(this.serverConfig);

        this.transactionStorage = TransactionStorageFactory.create(transactionStoreSerializer, this.serverConfig);

        this.scheduler = new RecoveryScheduler(this.serverConfig);

        this.recoveryExecutor = new ServerRecoveryExecutor(this.scheduler, this.transactionStoreSerializer, this.remotingServer);

        this.requestProcessor = new ServerRequestProcessor(this.scheduler, this.transactionStoreSerializer, this.transactionStorage);

        this.transactionStoreRecovery = new TransactionStoreRecovery(this.transactionStorage, this.recoveryExecutor, this.serverConfig);
        this.transactionStoreRecovery.setStoreMode(StorageMode.CENTRAL);
    }

    @Override
    @PostConstruct
    public void start() throws Exception {
        this.isShutdown = false;
        initializeRemotingServer();

        try {
            initializeRegistry();
        } catch (Exception e) {
            logger.error("failed to initialize registryService, stop the application!", e);
            StopUtils.stop();
        }

        ServerFlowMonitor.startMonitorScheduler(this.serverConfig.getFlowMonitorPrintIntervalMinutes());
    }

    @Override
    @PreDestroy
    public void shutdown() throws Exception {

        this.isShutdown = true;

        if (this.registryService != null) {
            this.registryService.close();
        }

        if (this.remotingServer != null) {
            this.remotingServer.shutdown();
        }

        if (this.scheduler != null) {
            this.scheduler.shutdown();
        }

        if (this.requestProcessExecutor != null) {
            this.requestProcessExecutor.shutdown();
        }

        if (this.transactionStoreRecovery != null) {
            this.transactionStoreRecovery.close();
        }

        this.transactionStoreSerializer = null;

        this.recoveryExecutor = null;
        this.requestProcessor = null;
        this.transactionStorage = null;
    }

    @Override
    public TransactionStoreRecovery getTransactionStoreRecovery() {
        return transactionStoreRecovery;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    private void initializeRemotingServer() {

        this.requestProcessExecutor = new ThreadPoolExecutor(serverConfig.getRequestProcessThreadSize(),
                serverConfig.getRequestProcessThreadSize(),
                1000 * 60, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(this.serverConfig.getRequestProcessThreadQueueCapacity()),
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("StoreTransactionThread_%d", threadIndex.getAndIncrement()));
                    }
                });

        remotingServer.registerDefaultProcessor(this.requestProcessor, this.requestProcessExecutor);

        remotingServer.registerChannelHandlers(new UnregisterScheduleHandler());

        remotingServer.start();
    }

    private void initializeRegistry() {
        InetSocketAddress inetSocketAddress;
        if (StringUtils.isNotEmpty(this.serverConfig.getRegistryAddress())) {
            inetSocketAddress = NetUtils.toInetSocketAddress(this.serverConfig.getRegistryAddress());
        } else {
            inetSocketAddress = new InetSocketAddress(NetUtils.getLocalAddress(), serverConfig.getListenPort());
        }
        this.registryService.start();
        this.registryService.register(inetSocketAddress);

        logger.info("succeeded to register with address {}", inetSocketAddress.toString());
    }

    public RecoveryScheduler getScheduler() {
        return scheduler;
    }

    public TransactionStorage getTransactionStorage() {
        return transactionStorage;
    }

    public RecoveryExecutor getRecoveryExecutor() {
        return recoveryExecutor;
    }

    @ChannelHandler.Sharable
    class UnregisterScheduleHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) {

            if (!isShutdown) {

                String domain = (String) ctx.channel().attr(AttributeKey.valueOf(MixAll.DOMAIN)).get();

                if (domain != null) {

                    Set<Channel> channels = FactoryBuilder.factoryOf(ChannelGroupMap.class).getInstance().getAllChannels(domain);

                    if (channels == null) {
                        return;
                    }

                    for (Channel channel : channels) {
                        if (channel != ctx.channel()) {
                            return;
                        }
                    }

                    //no other active channels, shutdown scheduler for the domain
                    scheduler.unregisterSchedule(domain);
                }
            }
        }
    }
}
