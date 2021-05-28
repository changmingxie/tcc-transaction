package org.mengyun.tcctransaction.recovery;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzRecoveryTask implements Job {
    public final static String RECOVERY_INSTANCE_KEY = "transactionRecovery";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TransactionRecovery transactionRecovery = (TransactionRecovery) context.getMergedJobDataMap().get(RECOVERY_INSTANCE_KEY);
        transactionRecovery.startRecover();
    }
}
