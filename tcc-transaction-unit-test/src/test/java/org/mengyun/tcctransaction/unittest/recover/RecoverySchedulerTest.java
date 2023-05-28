package org.mengyun.tcctransaction.unittest.recover;

import org.junit.Test;
import org.mengyun.tcctransaction.constants.MixAll;
import org.mengyun.tcctransaction.exception.SystemException;
import org.quartz.*;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author huabao.fang
 * @Date 2022/7/5 18:16
 */
public class RecoverySchedulerTest {

    public static final String JOB_NAME = "TccServerRecoverJob_%s";

    public static final String TRIGGER_NAME = "TccServerRecoveryTrigger_%s";

    private static final String DOMAIN = "TTTTTT";

    private static String quartzDataSourceDriver = "com.mysql.jdbc.Driver";

    private static String quartzDataSourceUrl = "jdbc:mysql://localhost:3306/TCC_SERVER?useSSL=false&allowPublicKeyRetrieval=true";

    private static String quartzDataSourceUser = "root";

    private static String quartzDataSourcePassword = "welcome1";

    private static String quartzDataSourceValidationQuery = "select 1";

    public static void main(String[] args) throws IOException {
        String domain = "eee6";
        Scheduler scheduler = createScheduler(domain);
        scheduleJob(scheduler, domain);
        start(scheduler);
        System.in.read();
    }

    private static void start(Scheduler scheduler) {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            throw new SystemException("quartz schedule job start failed", e);
        }
    }

    private static void scheduleJob(Scheduler scheduler, String domain) {
        String jobName = String.format(JOB_NAME, domain);
        String triggerName = String.format(TRIGGER_NAME, domain);
        JobDetail jobDetail = JobBuilder.newJob(QuartzDemoTask.class).withIdentity(jobName).build();
        jobDetail.getJobDataMap().put(MixAll.DOMAIN, domain);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName).withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ? ").withMisfireHandlingInstructionDoNothing()).build();
        try {
            if (!scheduler.checkExists(JobKey.jobKey(jobName))) {
                scheduler.scheduleJob(jobDetail, cronTrigger);
            }
        } catch (SchedulerException e) {
            throw new SystemException(String.format("register recovery task for domain<%s> failed", domain), e);
        }
    }

    private static Scheduler createScheduler(String domain) {
        Properties conf = new Properties();
        conf.put("org.quartz.scheduler.instanceName", domain);
        conf.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        conf.put("org.quartz.threadPool.threadCount", String.valueOf(Runtime.getRuntime().availableProcessors()));
        conf.put("org.quartz.scheduler.skipUpdateCheck", "false");
        conf.put("org.quartz.jobStore.isClustered", "true");
        conf.put("org.quartz.scheduler.instanceId", "AUTO");
        conf.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        conf.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        conf.put("org.quartz.jobStore.useProperties", "false");
        conf.put("org.quartz.jobStore.dataSource", "myDS");
        conf.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        conf.put("org.quartz.dataSource.myDS.driver", quartzDataSourceDriver);
        conf.put("org.quartz.dataSource.myDS.URL", quartzDataSourceUrl);
        conf.put("org.quartz.dataSource.myDS.user", quartzDataSourceUser);
        conf.put("org.quartz.dataSource.myDS.password", quartzDataSourcePassword);
        conf.put("org.quartz.dataSource.myDS.maxConnections", "2");
        conf.put("org.quartz.dataSource.myDS.validationQuery", quartzDataSourceValidationQuery);
        conf.put("org.quartz.jobStore.misfireThreshold", "1000");
        conf.put("org.quartz.scheduler.idleWaitTime", "5000");
        try {
            SchedulerFactory factory = new org.quartz.impl.StdSchedulerFactory(conf);
            return factory.getScheduler();
        } catch (SchedulerException e) {
            throw new SystemException("initialize recovery scheduler failed", e);
        }
    }

    @Test
    public void test1() throws IOException {
        startSchedulerAndJob(DOMAIN);
        System.in.read();
    }

    @Test
    public void test2() throws IOException {
        startSchedulerAndJob(DOMAIN);
        System.in.read();
    }

    @Test
    public void test3() throws IOException {
        startSchedulerAndJob(DOMAIN);
        System.in.read();
    }

    @Test
    public void test4() throws IOException {
        startSchedulerAndJob(DOMAIN);
        System.in.read();
    }

    @Test
    public void test_manage() throws IOException {
        String domain = DOMAIN;
        Scheduler scheduler = createScheduler(domain);
        scheduleJob(scheduler, domain);
        try {
            JobKey jobKey = new JobKey("TccServerRecoverJob_" + domain, "DEFAULT");
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            scheduler.pauseJob(jobKey);
            sleep(35 * 1000);
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        System.in.read();
    }

    private void startSchedulerAndJob(String domain) {
        Scheduler scheduler = createScheduler(domain);
        scheduleJob(scheduler, domain);
        start(scheduler);
    }

    private void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
