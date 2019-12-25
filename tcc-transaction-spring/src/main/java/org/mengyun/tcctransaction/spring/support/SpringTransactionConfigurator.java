package org.mengyun.tcctransaction.spring.support;

import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.recover.RecoverConfig;
import org.mengyun.tcctransaction.repository.CachableTransactionRepository;
import org.mengyun.tcctransaction.spring.recover.DefaultRecoverConfig;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by changmingxie on 11/11/15.
 */
public class SpringTransactionConfigurator implements TransactionConfigurator {

    private static volatile ExecutorService executorService = null;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired(required = false)
    private RecoverConfig recoverConfig = DefaultRecoverConfig.INSTANCE;


    private TransactionManager transactionManager;

    public void init() {
        transactionManager = new TransactionManager();
        transactionManager.setTransactionRepository(transactionRepository);

        if (executorService == null) {


            Executors.defaultThreadFactory();
            synchronized (SpringTransactionConfigurator.class) {

                if (executorService == null) {
                    executorService = new ThreadPoolExecutor(
                            recoverConfig.getAsyncTerminateThreadCorePoolSize(),
                            recoverConfig.getAsyncTerminateThreadMaxPoolSize(),
                            5L,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<Runnable>(recoverConfig.getAsyncTerminateThreadWorkQueueSize()),
                            new ThreadFactory() {

                                final AtomicInteger poolNumber = new AtomicInteger(1);
                                final ThreadGroup group;
                                final AtomicInteger threadNumber = new AtomicInteger(1);
                                final String namePrefix;

                                {
                                    SecurityManager securityManager = System.getSecurityManager();
                                    this.group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
                                    this.namePrefix = "tcc-async-terminate-pool-" + poolNumber.getAndIncrement() + "-thread-";
                                }

                                public Thread newThread(Runnable runnable) {
                                    Thread thread = new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
                                    if (thread.isDaemon()) {
                                        thread.setDaemon(false);
                                    }

                                    if (thread.getPriority() != 5) {
                                        thread.setPriority(5);
                                    }

                                    return thread;
                                }
                            },
                            new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }
        }

        transactionManager.setExecutorService(executorService);

        if (transactionRepository instanceof CachableTransactionRepository) {
            ((CachableTransactionRepository) transactionRepository).setExpireDuration(recoverConfig.getRecoverDuration());
        }
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    @Override
    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }
}
