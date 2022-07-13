package org.mengyun.tcctransaction.recovery;

import org.mengyun.tcctransaction.TccService;
import org.mengyun.tcctransaction.constants.MixAll;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class QuartzRecoveryTask implements Job {

    static final Logger logger = LoggerFactory.getLogger(QuartzRecoveryTask.class.getSimpleName());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String domain = context.getJobDetail().getJobDataMap().getString(MixAll.DOMAIN);
        FactoryBuilder.factoryOf(TccService.class).getInstance().getTransactionStoreRecovery().startRecover(domain);
    }
}
