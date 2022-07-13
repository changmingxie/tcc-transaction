package org.mengyun.tcctransaction.unittest.recover;

import lombok.SneakyThrows;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Author huabao.fang
 * @Date 2022/7/5 19:24
 **/
@DisallowConcurrentExecution
public class QuartzDemoTask implements Job {

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        System.out.println("executing..." + context.getScheduler().getSchedulerInstanceId());

    }
}
