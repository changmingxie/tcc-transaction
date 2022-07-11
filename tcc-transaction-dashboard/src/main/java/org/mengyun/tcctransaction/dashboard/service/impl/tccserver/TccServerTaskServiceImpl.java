package org.mengyun.tcctransaction.dashboard.service.impl.tccserver;

import org.mengyun.tcctransaction.TccClient;
import org.mengyun.tcctransaction.dashboard.dto.ModifyCronDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.dto.TaskDto;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.exception.TransactionException;
import org.mengyun.tcctransaction.dashboard.service.DomainService;
import org.mengyun.tcctransaction.dashboard.service.TaskService;
import org.mengyun.tcctransaction.dashboard.service.condition.TccServerStorageCondition;
import org.mengyun.tcctransaction.utils.CollectionUtils;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 11:51
 **/
@Conditional(TccServerStorageCondition.class)
@Service
public class TccServerTaskServiceImpl implements TaskService {

    private Logger logger = LoggerFactory.getLogger(TccServerTaskServiceImpl.class.getSimpleName());

    @Autowired
    private TccClient tccClient;

    @Autowired
    private DomainService domainService;

    @Override
    public ResponseDto<List<TaskDto>> all() {
        List<String> domainList = domainService.getAllDomainKeys().getData();
        try {
            return ResponseDto.returnSuccess(buildTaskList(domainList));
        } catch (SchedulerException e) {
            logger.error("task all error", e);
            return ResponseDto.returnFail(ResponseCodeEnum.TASK_OPERATE_ERROR);
        }
    }

    @Override
    public ResponseDto pause(String domain) {
        try {
            tccClient.getScheduler().getScheduler(domain).pauseJob(selectJobKey(domain));
            logger.info("domain:{} task paused", domain);
        } catch (SchedulerException e) {
            logger.error("pasuse job for domain:{} error", domain, e);
            return ResponseDto.returnFail(ResponseCodeEnum.TASK_OPERATE_ERROR);
        }
        return ResponseDto.returnSuccess();
    }

    @Override
    public ResponseDto resume(String domain) {
        try {
            tccClient.getScheduler().getScheduler(domain).resumeJob(selectJobKey(domain));
            logger.info("domain:{} task resumed", domain);
        } catch (SchedulerException e) {
            logger.error("resume job for domain:{} error", domain, e);
            return ResponseDto.returnFail(ResponseCodeEnum.TASK_OPERATE_ERROR);
        }
        return ResponseDto.returnSuccess();
    }

    @Override
    public ResponseDto modifyCron(ModifyCronDto requestDto) {
        try {
            TriggerKey triggerKey = selectTriggerKey(requestDto.getDomain());
            CronTrigger currentCronTrigger = (CronTrigger) tccClient.getScheduler().getScheduler(requestDto.getDomain()).getTrigger(triggerKey);
            String currentCron = currentCronTrigger.getCronExpression();
            if (StringUtils.isNotEmpty(requestDto.getCronExpression())
                    && !requestDto.getCronExpression().equals(currentCron)) {
                CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey.getName())
                        .withSchedule(CronScheduleBuilder.cronSchedule(requestDto.getCronExpression())
                                .withMisfireHandlingInstructionDoNothing()).build();
                tccClient.getScheduler().getScheduler(requestDto.getDomain()).rescheduleJob(triggerKey, cronTrigger);
            }
            logger.info("domain:{} update cron from {} to {} success", requestDto.getDomain(), currentCron, requestDto.getCronExpression());
        } catch (SchedulerException e) {
            logger.error("modifyCron error", e);
            return ResponseDto.returnFail(ResponseCodeEnum.TASK_MODIFY_CRON_ERROR);
        }

        return ResponseDto.returnSuccess();
    }

    @Override
    public ResponseDto delete(String domain) {
        try {
            JobKey jobKey = selectJobKey(domain);
            tccClient.getScheduler().getScheduler(domain).deleteJob(jobKey);
            logger.info("domain:{} task deleted", domain);
        } catch (SchedulerException e) {
            logger.error("delete job for domain:{} error", domain, e);
            return ResponseDto.returnFail(ResponseCodeEnum.TASK_MODIFY_CRON_ERROR);
        }
        return ResponseDto.returnSuccess();
    }

    private JobKey selectJobKey(String domain) throws SchedulerException {
        Set<JobKey> jobKeySet = tccClient.getScheduler().getScheduler(domain).getJobKeys(GroupMatcher.anyGroup());
        if (CollectionUtils.isEmpty(jobKeySet)) {
            throw new TransactionException(ResponseCodeEnum.TASK_STATUS_ERROR);
        }
        return jobKeySet.iterator().next();
    }

    private TriggerKey selectTriggerKey(String domain) throws SchedulerException {
        Set<TriggerKey> triggerKeySet = tccClient.getScheduler().getScheduler(domain).getTriggerKeys(GroupMatcher.anyGroup());
        if (CollectionUtils.isEmpty(triggerKeySet)) {
            throw new TransactionException(ResponseCodeEnum.TASK_STATUS_ERROR);
        }
        return triggerKeySet.iterator().next();
    }

    private List<TaskDto> buildTaskList(List<String> domainList) throws SchedulerException {
        List<TaskDto> taskDtoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(domainList)) {
            return taskDtoList;
        }
        for (String domain : domainList) {
            Scheduler scheduler = tccClient.getScheduler().registerScheduleIfNotPresent(domain);
            Set<JobKey> jobKeySet = tccClient.getScheduler().getScheduler(domain).getJobKeys(GroupMatcher.anyGroup());
            if (CollectionUtils.isEmpty(jobKeySet)) {
                continue;
            }
            JobKey jobKey = jobKeySet.iterator().next();
            Set<TriggerKey> triggerKeySet = tccClient.getScheduler().getScheduler(domain).getTriggerKeys(GroupMatcher.anyGroup());
            if (CollectionUtils.isEmpty(triggerKeySet)) {
                continue;
            }
            TriggerKey triggerKey = triggerKeySet.iterator().next();
            CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (cronTrigger == null) {
                continue;
            }
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            taskDtoList.add(new TaskDto(scheduler.getSchedulerName(), domain, jobKey.getGroup(), jobKey.getName(), triggerState.name(), cronTrigger.getCronExpression()));
        }
        return taskDtoList;
    }
}
