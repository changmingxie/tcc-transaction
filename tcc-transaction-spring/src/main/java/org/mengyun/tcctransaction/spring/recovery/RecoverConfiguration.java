package org.mengyun.tcctransaction.spring.recovery;

import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.TransactionOptimisticLockException;
import org.mengyun.tcctransaction.recovery.DefaultRecoverFrequency;
import org.mengyun.tcctransaction.recovery.RecoverFrequency;
import org.mengyun.tcctransaction.recovery.RecoveryLock;
import org.mengyun.tcctransaction.recovery.TransactionRecovery;
import org.mengyun.tcctransaction.repository.CacheableTransactionRepository;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by changming.xie on 11/25/17.
 */
public class RecoverConfiguration implements TransactionConfigurator {

    private TransactionManager transactionManager;
    private TransactionRepository transactionRepository;
    private RecoverFrequency recoverFrequency = DefaultRecoverFrequency.INSTANCE;
    private RecoveryLock recoveryLock = RecoveryLock.DEFAULT_LOCK;

    private Scheduler scheduler;
    private String jobName;
    private String triggerName;
    private int threadCount = Runtime.getRuntime().availableProcessors();

    private int asyncTerminateThreadPoolSize = threadCount * 2 + 1;

    private int asyncTerminateThreadQueueSize = 1024;

    public RecoverConfiguration() {
    }

    @PostConstruct
    public void init() throws Exception {
        transactionManager = new TransactionManager();
        transactionManager.setTransactionRepository(transactionRepository);

        transactionManager.setExecutorService(new ThreadPoolExecutor(asyncTerminateThreadPoolSize,
                asyncTerminateThreadPoolSize,
                0l,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(asyncTerminateThreadQueueSize), new ThreadPoolExecutor.AbortPolicy()));

        if (transactionRepository instanceof CacheableTransactionRepository) {
            ((CacheableTransactionRepository) transactionRepository).setExpireDuration(recoverFrequency.getRecoverDuration());
        }

        TransactionRecovery transactionRecovery = new TransactionRecovery();
        transactionRecovery.setTransactionConfigurator(this);

        RecoverScheduledJob recoveryScheduledJob = new RecoverScheduledJob();
        recoveryScheduledJob.setJobName(StringUtils.isEmpty(jobName) ? "compensableRecoverJob" : jobName);
        recoveryScheduledJob.setTriggerName(StringUtils.isEmpty(triggerName) ? "compensableTrigger" : triggerName);

        recoveryScheduledJob.setTransactionRecovery(transactionRecovery);
        recoveryScheduledJob.setCronExpression(getRecoverFrequency().getCronExpression());
        recoveryScheduledJob.setDelayStartSeconds(recoverFrequency.getRecoverDuration());

        Properties conf = new Properties();
        conf.put("org.quartz.threadPool.threadCount", String.valueOf(threadCount));
        conf.put("org.quartz.scheduler.instanceName", "recovery-quartz");

        if (scheduler == null) {
            SchedulerFactory factory = new org.quartz.impl.StdSchedulerFactory(conf);
            scheduler = factory.getScheduler();
        }

        recoveryScheduledJob.setScheduler(scheduler);
        recoveryScheduledJob.init();
    }

    @PreDestroy
    public void close() throws Exception {
        if (scheduler != null) {
            scheduler.shutdown();
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

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public RecoverFrequency getRecoverFrequency() {
        return recoverFrequency;
    }

    public void setRecoverFrequency(RecoverFrequency recoverFrequency) {
        this.recoverFrequency = recoverFrequency;
    }

    @Override
    public Lock getRecoveryLock() {
        return this.recoveryLock;
    }

    public void setRecoveryLock(RecoveryLock recoveryLock) {
        this.recoveryLock = recoveryLock;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

}
