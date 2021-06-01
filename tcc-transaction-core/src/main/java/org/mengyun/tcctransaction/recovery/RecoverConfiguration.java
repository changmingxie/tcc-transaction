package org.mengyun.tcctransaction.recovery;

import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;
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

    public RecoverConfiguration() {
    }

    @PostConstruct
    public void init() throws Exception {
        transactionManager = new TransactionManager();
        transactionManager.setTransactionRepository(transactionRepository);

        TransactionRecovery transactionRecovery = new TransactionRecovery();
        transactionRecovery.setTransactionConfigurator(this);

        RecoverScheduledJob recoveryScheduledJob = new RecoverScheduledJob();
        recoveryScheduledJob.setJobName("compensableRecoverJob");
        recoveryScheduledJob.setTriggerName("compensableTrigger");

        recoveryScheduledJob.setTransactionRecovery(transactionRecovery);
        recoveryScheduledJob.setCronExpression(getRecoverFrequency().getCronExpression());
        recoveryScheduledJob.setDelayStartSeconds(recoverFrequency.getRecoverDuration());

        Properties conf = new Properties();
        conf.put("org.quartz.threadPool.threadCount", String.valueOf(Runtime.getRuntime().availableProcessors()));
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
}
