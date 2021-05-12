package org.mengyun.tcctransaction.spring.recovery;

import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.recovery.TransactionRecovery;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/**
 * Created by changming.xie on 6/2/16.
 */
public class RecoverScheduledJob {

    private TransactionRecovery transactionRecovery;

    private Scheduler scheduler;

    private String jobName;

    private String triggerName;

    private String cronExpression;


    public void init() {

        try {
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            jobDetail.setTargetObject(transactionRecovery);
            jobDetail.setTargetMethod("startRecover");

            jobDetail.setName(jobName);

            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();

            CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();

            cronTrigger.setBeanName(triggerName);

            cronTrigger.setCronExpression(cronExpression);
            cronTrigger.setJobDetail(jobDetail.getObject());
            cronTrigger.afterPropertiesSet();

            scheduler.scheduleJob(jobDetail.getObject(), cronTrigger.getObject());

            scheduler.start();

        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    public void setTransactionRecovery(TransactionRecovery transactionRecovery) {
        this.transactionRecovery = transactionRecovery;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
