package org.mengyun.tcctransaction.unittest;


import com.google.common.collect.Lists;
import com.xfvape.uid.UidGenerator;
import com.xfvape.uid.impl.CachedUidGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mengyun.tcctransaction.ClientConfig;
import org.mengyun.tcctransaction.ServerConfig;
import org.mengyun.tcctransaction.TccServer;
import org.mengyun.tcctransaction.properties.registry.ServerRegistryProperties;
import org.mengyun.tcctransaction.remoting.netty.NettyRemotingClient;
import org.mengyun.tcctransaction.remoting.netty.ServerAddressLoader;
import org.mengyun.tcctransaction.serializer.*;
import org.mengyun.tcctransaction.serializer.kryo.RegisterableKryoTransactionSerializer;
import org.mengyun.tcctransaction.spring.factory.SpringBeanFactory;
import org.mengyun.tcctransaction.spring.xid.DefaultUUIDGenerator;
import org.mengyun.tcctransaction.spring.xid.SimpleWorkerIdAssigner;
import org.mengyun.tcctransaction.storage.RemotingTransactionStorage;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.transaction.Transaction;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.mengyun.tcctransaction.xid.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


@RunWith(SpringRunner.class)
//@Ignore
@Import(RemotingStorageTest.TestConfig.class)
public class RemotingStorageTest {

    private static final Logger logger = LoggerFactory.getLogger(RemotingStorageTest.class);
    private static TransactionSerializer transactionSerializer = new RegisterableKryoTransactionSerializer();
    private static TransactionStoreSerializer transactionStoreSerializer = new TccTransactionStoreSerializer();
    private static RemotingCommandSerializer remotingCommandSerializer = new TccRemotingCommandSerializer();
    private TccServer tccServer;

    @Before
    public void init() throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        ServerRegistryProperties serverRegistryProperties = new ServerRegistryProperties();
        serverRegistryProperties.setRegistryAddress("127.0.0.1:2332");
        serverConfig.setServerRegistryConfig(serverRegistryProperties);
        this.tccServer = new TccServer(serverConfig);
        tccServer.start();
    }

    @After
    public void close() throws Exception {
        if (tccServer != null) {
            tccServer.shutdown();
        }
    }

    @Test
    public void performance_test_remoting_transaction_storage_crud() throws InterruptedException {

        ClientConfig clientConfig = ClientConfig.DEFAULT;


        NettyRemotingClient remotingClient = new NettyRemotingClient(remotingCommandSerializer, clientConfig, new ServerAddressLoader() {
            @Override
            public InetSocketAddress selectOne(String key) {
                return NetUtils.toInetSocketAddress(clientConfig.getDirectRegistryProperties().getServerAddresses());
            }

            @Override
            public List<InetSocketAddress> getAll(String key) {
                return Lists.newArrayList(NetUtils.toInetSocketAddress(clientConfig.getDirectRegistryProperties().getServerAddresses()));
            }

            @Override
            public boolean isAvailableAddress(InetSocketAddress remoteAddress) {
                return NetUtils.toInetSocketAddress(clientConfig.getDirectRegistryProperties().getServerAddresses()).equals(remoteAddress);
            }
        });

        remotingClient.start();

        RemotingTransactionStorage repository = new RemotingTransactionStorage(transactionStoreSerializer, clientConfig);
        repository.setRemotingClient(remotingClient);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);

        int totalCount = 1;
        AtomicInteger index = new AtomicInteger(0);

        long startTimeMillis = System.currentTimeMillis();

        doCreate(repository, executorService, totalCount, index);

        // restart the server side

        //doCreate(repository, executorService, totalCount, index);

        repository.close();

        logger.info("total run: " + index.get() + " with cost time:" + (System.currentTimeMillis() - startTimeMillis) / 1000.000);
    }


    private void doCreate(RemotingTransactionStorage repository, ExecutorService executorService, int totalCount, AtomicInteger index) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(totalCount);
        for (int i = 0; i < totalCount; i++) {
            int finalI = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {

                        Transaction transaction = new Transaction("TCC:TEST");

                        TransactionStore transactionStore = new TransactionStore();
                        transactionStore.setDomain("TCC:TEST");
                        transactionStore.setRootDomain(transaction.getRootDomain());
                        transactionStore.setRootXid(transaction.getRootXid());
                        transactionStore.setXid(transaction.getXid());
                        transactionStore.setStatusId(transaction.getStatus().getId());
                        transactionStore.setContent(transactionSerializer.serialize(transaction));
                        repository.create(transactionStore);
                        repository.update(transactionStore);
                        repository.findByXid(transactionStore.getDomain(), transactionStore.getXid());
                        repository.delete(transactionStore);
                        logger.debug("run done: " + index.incrementAndGet());
                    } catch (Exception e) {
                        logger.error("run failed.", e);
                    } finally {
                        countDownLatch.countDown();
                    }

                }
            });
        }
        countDownLatch.await();
    }

    @TestConfiguration
    public static class TestConfig{

        @Bean("springBeanFactory")
        public SpringBeanFactory getSpringBeanFactory() {
            return new SpringBeanFactory();
        }

        @Bean
        public UidGenerator uidGenerator() {
            int timeBits = 28;
            int workBits = 22;
            int seqBits = 13;
            CachedUidGenerator cachedUidGenerator = new CachedUidGenerator();
            cachedUidGenerator.setEpochStr("2022-01-01");
            cachedUidGenerator.setWorkerBits(workBits);
            cachedUidGenerator.setTimeBits(timeBits);
            cachedUidGenerator.setSeqBits(seqBits);
            cachedUidGenerator.setWorkerIdAssigner(new SimpleWorkerIdAssigner(workBits));
            return cachedUidGenerator;
        }

        @Bean
        public UUIDGenerator uuidGenerator() {
            return new DefaultUUIDGenerator(uidGenerator());
        }
    }
}
