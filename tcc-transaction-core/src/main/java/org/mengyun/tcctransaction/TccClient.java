package org.mengyun.tcctransaction;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mengyun.tcctransaction.constants.RemotingServiceCode;
import org.mengyun.tcctransaction.discovery.loadbalance.LoadBalanceFactory;
import org.mengyun.tcctransaction.discovery.loadbalance.LoadBalanceServcie;
import org.mengyun.tcctransaction.discovery.registry.RegistryFactory;
import org.mengyun.tcctransaction.discovery.registry.RegistryService;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.processor.ClientRecoveryExecutor;
import org.mengyun.tcctransaction.processor.ClientRecoveryProcessor;
import org.mengyun.tcctransaction.recovery.RecoveryExecutor;
import org.mengyun.tcctransaction.recovery.RecoveryScheduler;
import org.mengyun.tcctransaction.recovery.TransactionStoreRecovery;
import org.mengyun.tcctransaction.remoting.RemotingClient;
import org.mengyun.tcctransaction.remoting.RequestProcessor;
import org.mengyun.tcctransaction.remoting.netty.NettyRemotingClient;
import org.mengyun.tcctransaction.remoting.netty.ServerAddressLoader;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;
import org.mengyun.tcctransaction.repository.DefaultTransactionRepository;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.serializer.*;
import org.mengyun.tcctransaction.serializer.json.FastjsonTransactionSerializer;
import org.mengyun.tcctransaction.serializer.kryo.RegisterableKryoTransactionSerializer;
import org.mengyun.tcctransaction.storage.*;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.transaction.TransactionManager;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.mengyun.tcctransaction.utils.StopUtils;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by changming.xie on 11/25/17.
 */
public class TccClient implements TccService {

    static final Logger logger = LoggerFactory.getLogger(TccClient.class.getSimpleName());
    //attribute inject
    private ClientConfig clientConfig = ClientConfig.DEFAULT;
    //attribute inject
    private TransactionStorage transactionStorage;
    //attribute inject
    private RecoveryExecutor recoveryExecutor;
    //attribute inject
    private RequestProcessor requestProcessor;

    private TransactionStoreRecovery transactionStoreRecovery;

    private ExecutorService requestProcessExecutor;

    private TransactionStoreSerializer transactionStoreSerializer;

    private RecoveryScheduler scheduler;

    private RemotingClient remotingClient;

    //attribute inject
    private TransactionManager transactionManager;
    //attribute inject
    private TransactionRepository transactionRepository;

    private TransactionSerializer transactionSerializer;

    private RemotingCommandSerializer remotingCommandSerializer;

    private RegistryService registryService;

    private LoadBalanceServcie loadBalanceServcie;

    private volatile boolean isStarting = false;

    public TccClient(ClientConfig clientConfig) {
        if (clientConfig != null) {
            this.clientConfig = clientConfig;
        }

        if (this.clientConfig.getSerializerType() == null) {
            throw new SystemException("serializerType should not be null");
        }
        switch (this.clientConfig.getSerializerType()) {
            case KRYO: {
                if (this.clientConfig.getKryoPoolSize() > 0) {
                    this.transactionSerializer = new RegisterableKryoTransactionSerializer(this.clientConfig.getKryoPoolSize());
                } else {
                    this.transactionSerializer = new RegisterableKryoTransactionSerializer();
                }
                break;
            }
            case FASTJSON: {
                this.transactionSerializer = new FastjsonTransactionSerializer();
                break;
            }
            case CUSTOMIZED: {
                if (StringUtils.isBlank(this.clientConfig.getTransactionSerializerClassName())) {
                    throw new SystemException("transactionSerializerClassName should not be null");
                }
                try {
                    Class<? extends TransactionSerializer> transactionSerializerClass = (Class<? extends TransactionSerializer>) Class.forName(this.clientConfig.getTransactionSerializerClassName());
                    this.transactionSerializer = FactoryBuilder.factoryOf(transactionSerializerClass).getInstance();
                } catch (ClassNotFoundException e) {
                    throw new SystemException(e);
                }
                break;
            }
            default:
                throw new SystemException(String.format("invalid serializerType: %s", this.clientConfig.getSerializerType()));
        }

        this.transactionStoreSerializer = new TccTransactionStoreSerializer();
        this.remotingCommandSerializer = new TccRemotingCommandSerializer();

        if (this.clientConfig.getStorageType() == StorageType.REMOTING) {
            this.registryService = RegistryFactory.getInstance(this.clientConfig);
            this.loadBalanceServcie = LoadBalanceFactory.getInstance(this.clientConfig);
            remotingClient = new NettyRemotingClient(this.remotingCommandSerializer, this.clientConfig,
                    new ServerAddressLoader() {
                        @Override
                        public String selectOneAvailableAddress() {
                            return loadBalanceServcie.select(registryService.lookup());
                        }

                        @Override
                        public List<String> getAllAvailableAddresses() {
                            return registryService.lookup();
                        }

                        @Override
                        public boolean isAvailableAddress(String address) {
                            List<String> serverAddresses = registryService.lookup();
                            return serverAddresses.contains(address);
                        }
                    });
        }

        this.transactionStorage = TransactionStorageFactory.create(transactionStoreSerializer, this.clientConfig);

        this.transactionRepository = new DefaultTransactionRepository(this.clientConfig.getDomain(), transactionSerializer, this.transactionStorage);

        this.scheduler = new RecoveryScheduler(this.clientConfig);

        this.recoveryExecutor = new ClientRecoveryExecutor(transactionSerializer, this.transactionRepository);

        this.requestProcessor = new ClientRecoveryProcessor(this.transactionStoreSerializer, this.recoveryExecutor);

        if (transactionRepository.supportRecovery() && this.clientConfig.isRecoveryEnabled()) {
            transactionStoreRecovery = new TransactionStoreRecovery(this.transactionStorage, this.recoveryExecutor, this.clientConfig);
            transactionStoreRecovery.setStoreMode(this.clientConfig.getStorageMode());
        }

        this.transactionManager = new TransactionManager(this.transactionRepository);
    }

    @Override
    @PostConstruct
    public void start() throws Exception {
        this.isStarting = true;

        if (this.clientConfig.getStorageType() == StorageType.REMOTING) {
            try {
                this.registryService.start();
                this.registryService.subscribe();
            } catch (Exception e) {
                logger.error("failed to initialize registryService, stop the application!", e);
                StopUtils.stop();
            }
            initializeRemotingClient();
        } else {

            registerDomain(this.clientConfig.getDomain());
            if (transactionRepository.supportRecovery() && this.clientConfig.isRecoveryEnabled()) {
                scheduler.registerScheduleAndStartIfNotPresent(this.clientConfig.getDomain());
            }
        }

        this.isStarting = false;
    }

    @Override
    @PreDestroy
    public void shutdown() throws Exception {

        if (this.clientConfig.getStorageType() == StorageType.REMOTING) {
            this.remotingClient.shutdown();
            this.remotingClient = null;

            if (this.registryService != null) {
                this.registryService.close();
                this.registryService = null;
            }
            this.loadBalanceServcie = null;
        }

        if (scheduler != null) {
            scheduler.shutdown();
        }

        if (this.requestProcessExecutor != null) {
            this.requestProcessExecutor.shutdown();
        }

        if (this.transactionStoreRecovery != null) {
            this.transactionStoreRecovery.close();
        }

        if (this.transactionRepository != null) {
            this.transactionRepository.close();
        }

        this.transactionSerializer = null;
        this.transactionStoreSerializer = null;
        this.remotingCommandSerializer = null;

        this.transactionManager = null;
        this.recoveryExecutor = null;
        this.requestProcessor = null;
        this.transactionStorage = null;
    }

    @Override
    public TransactionStoreRecovery getTransactionStoreRecovery() {
        return transactionStoreRecovery;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public TransactionStorage getTransactionStorage() {
        return transactionStorage;
    }

    public RecoveryScheduler getScheduler() {
        return scheduler;
    }

    private void initializeRemotingClient() {

        if (this.transactionStorage instanceof RetryableTransactionStorage) {
            RemotingTransactionStorage remotingTransactionStorage = ((RemotingTransactionStorage) ((RetryableTransactionStorage) this.transactionStorage).getTargetTransactionStorage());
            remotingTransactionStorage.setRemotingClient(this.remotingClient);
        } else {
            ((RemotingTransactionStorage) this.transactionStorage).setRemotingClient(this.remotingClient);
        }

        this.requestProcessExecutor = new ThreadPoolExecutor(this.clientConfig.getRequestProcessThreadSize(),
                clientConfig.getRequestProcessThreadSize(),
                1000 * 60, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(this.clientConfig.getRequestProcessThreadQueueCapacity()),
                new ThreadFactory() {
                    private final AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("StoreTransactionThread_%d", threadIndex.getAndIncrement()));
                    }
                });

        this.remotingClient.registerDefaultProcessor(requestProcessor, this.requestProcessExecutor);

        this.remotingClient.registerChannelHandlers(new RegisterToServerHandler());

        this.remotingClient.start();

        registerToServer();
    }

    private void registerToServer() {
        RemotingCommand registerCommand = RemotingCommand.createCommand(RemotingCommandCode.SERVICE_REQ, null);
        registerCommand.setServiceCode(RemotingServiceCode.REGISTER);
        registerCommand.setBody(clientConfig.getDomain().getBytes());
        try {
            remotingClient.invokeOneway(registerCommand, clientConfig.getRequestTimeoutMillis());
        } catch (Exception e) {
            logger.error("failled to register to server", e);
        }
    }

    private void registerToServer(String address) {
        RemotingCommand registerCommand = RemotingCommand.createCommand(RemotingCommandCode.SERVICE_REQ, null);
        registerCommand.setServiceCode(RemotingServiceCode.REGISTER);
        registerCommand.setBody(clientConfig.getDomain().getBytes());
        try {
            remotingClient.invokeOneway(address, registerCommand, clientConfig.getRequestTimeoutMillis());
        } catch (Exception e) {
            logger.error("failled to register to server", e);
        }
    }

    private void registerDomain(String domain) {
        if (transactionStorage.supportStorageRecoverable()) {
            ((StorageRecoverable) transactionStorage).registerDomain(new DomainStore(domain));
        } else {
            logger.warn("transactionStorage:{} not StorageRecoverable, do not regist domain", transactionStorage.getClass().getSimpleName());
        }
    }

    @ChannelHandler.Sharable
    class RegisterToServerHandler extends ChannelInboundHandlerAdapter {

        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            //register to server if connected
            if (!isStarting) {
                ctx.channel().eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        registerToServer(NetUtils.parseSocketAddress(ctx.channel().remoteAddress()));
                    }
                });
            }
        }
    }
}
