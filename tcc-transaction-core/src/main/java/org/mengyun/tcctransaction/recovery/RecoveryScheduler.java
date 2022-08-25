package org.mengyun.tcctransaction.recovery;

import org.mengyun.tcctransaction.constants.MixAll;
import org.mengyun.tcctransaction.exception.SystemException;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changming.xie on 6/2/16.
 */
public class RecoveryScheduler {

    public static final String JOB_NAME = "TCC_JOB_%s";
    public static final String TRIGGER_NAME = "TCC_TRIGGER_%s";
    private static final Logger logger = LoggerFactory.getLogger(RecoveryScheduler.class.getSimpleName());
    private RecoveryConfig recoveryConfig;

    private Map<String, Scheduler> schedulers = new ConcurrentHashMap<>();

    public RecoveryScheduler(RecoveryConfig recoveryConfig) {
        this.recoveryConfig = recoveryConfig;
    }

    public void registerScheduleAndStartIfNotPresent(String domain) {
        Scheduler scheduler = registerScheduleIfNotPresent(domain);
        start(scheduler);
    }

    public Scheduler registerScheduleIfNotPresent(String domain) {
        if (!schedulers.containsKey(domain)) {
            synchronized (RecoveryScheduler.class) {
                if (!schedulers.containsKey(domain)) {
                    Scheduler scheduler = createScheduler(domain);
                    scheduleJob(scheduler, domain);
                    schedulers.put(domain, scheduler);
                }
            }
        }
        return schedulers.get(domain);
    }

    public void unregisterSchedule(String domain) {
        if (!schedulers.containsKey(domain)) {
            return;
        }

        synchronized (RecoveryScheduler.class) {
            Scheduler scheduler = schedulers.get(domain);
            if (scheduler == null) {
                return;
            }
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                logger.error(String.format("shutdown scheduler<%s> failed.", domain), e);
            } finally {
                schedulers.remove(domain);
            }
        }
    }

    public void shutdown() throws SchedulerException {
        for (Scheduler scheduler : schedulers.values()) {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        }
        schedulers.clear();
    }


    public Scheduler getScheduler(String domain) {
        return schedulers.get(domain);
    }

    private void scheduleJob(Scheduler scheduler, String domain) {

        String jobName = String.format(JOB_NAME, domain);
        String triggerName = String.format(TRIGGER_NAME, domain);

        JobDetail jobDetail = JobBuilder.newJob(QuartzRecoveryTask.class).withIdentity(jobName).build();
        jobDetail.getJobDataMap().put(MixAll.DOMAIN, domain);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName)
                .withSchedule(CronScheduleBuilder.cronSchedule(recoveryConfig.getCronExpression())
                        .withMisfireHandlingInstructionDoNothing()).build();

        try {
            if (!scheduler.checkExists(JobKey.jobKey(jobName))) {
                scheduler.scheduleJob(jobDetail, cronTrigger);
            } else {
                if (recoveryConfig.isUpdateJobForcibly()) {
                    scheduler.deleteJob(JobKey.jobKey(jobName));
                    scheduler.scheduleJob(jobDetail, cronTrigger);
                }
            }
        } catch (SchedulerException se) {
            try {
                scheduler.shutdown();
            } catch (Exception ignore) {
                //ignore
            }
            throw new SystemException(String.format("register recovery task for domain<%s> failed", domain), se);
        }
    }

    private void start(Scheduler scheduler) {
        String schedulerName = null;
        try {
            schedulerName = scheduler.getSchedulerName();
            if (!scheduler.isStarted()) {
                scheduler.startDelayed(recoveryConfig.getRecoverDuration());
            }
        } catch (SchedulerException e) {
            throw new SystemException(String.format("start scheduler<%s> failed.", schedulerName), e);
        }
    }

    private Scheduler createScheduler(String domain) {
        Properties conf = new Properties();
        conf.put("org.quartz.scheduler.instanceName", domain);
        conf.put("org.quartz.threadPool.class", ReusableThreadPool.class.getName());
        conf.put("org.quartz.threadPool.threadCount", String.valueOf(recoveryConfig.getQuartzThreadPoolThreadCount()));
        conf.put("org.quartz.scheduler.skipUpdateCheck", "false");

        if (recoveryConfig.isQuartzClustered()) {
            conf.put("org.quartz.jobStore.isClustered", String.valueOf(recoveryConfig.isQuartzClustered()));
            conf.put("org.quartz.scheduler.instanceId", "AUTO");
            conf.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
            conf.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
            conf.put("org.quartz.jobStore.useProperties", "false");
            conf.put("org.quartz.jobStore.dataSource", "myDS");
            conf.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
            conf.put("org.quartz.dataSource.myDS.connectionProvider.class", ReusableConnectionProvider.class.getName());
            conf.put("org.quartz.dataSource.myDS.driver", recoveryConfig.getQuartzDataSourceDriver());
            conf.put("org.quartz.dataSource.myDS.URL", recoveryConfig.getQuartzDataSourceUrl());
            conf.put("org.quartz.dataSource.myDS.user", recoveryConfig.getQuartzDataSourceUser());
            conf.put("org.quartz.dataSource.myDS.password", recoveryConfig.getQuartzDataSourcePassword());
            conf.put("org.quartz.dataSource.myDS.initialPoolSize", String.valueOf(recoveryConfig.getQuartzDataSourceInitialPoolSize()));
            conf.put("org.quartz.dataSource.myDS.minPoolSize", String.valueOf(recoveryConfig.getQuartzDataSourceMinPoolSize()));
            conf.put("org.quartz.dataSource.myDS.maxPoolSize", String.valueOf(recoveryConfig.getQuartzDataSourceMaxPoolSize()));
            conf.put("org.quartz.dataSource.myDS.validationQuery", recoveryConfig.getQuartzDataSourceValidationQuery());
            conf.put("org.quartz.dataSource.myDS.checkoutTimeout", String.valueOf(recoveryConfig.getQuartzDataSourceCheckoutTimeout()));
            conf.put("org.quartz.jobStore.misfireThreshold", "1000");
            conf.put("org.quartz.scheduler.idleWaitTime", "5000");
        }

        try {
            SchedulerFactory factory = new org.quartz.impl.StdSchedulerFactory(conf);
            return factory.getScheduler();
        } catch (SchedulerException e) {
            throw new SystemException("initialize recovery scheduler failed", e);
        }
    }
}
