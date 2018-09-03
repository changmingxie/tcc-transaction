package org.mengyun.tcctransaction.nutz.recover;

import org.mengyun.tcctransaction.recover.TransactionRecovery;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 恢复定时任务
 * @author  liangcz
 * @Date    2018年8月13日 下午4:58:56
 * @version 1.0
 */
@IocBean
public class RecoverScheduledJob implements Job{
	@Inject
    private TransactionRecovery transactionRecovery;
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// Mvcs.ctx().getDefaultIoc().get(TransactionConfigurator.class,"transactionConfigurator")
		transactionRecovery.startRecover();
	}
}
