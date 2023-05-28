package org.mengyun.tcctransaction.dashboard.dto;

/**
 * @Author huabao.fang
 * @Date 2022/5/24 18:29
 */
public class TaskDto {

    private String schedulerName;

    private String domain;

    private String jobGroup;

    private String jobName;

    private String status;

    private String cronExpression;

    public TaskDto() {
    }

    public TaskDto(String schedulerName, String domain, String jobGroup, String jobName, String status, String cronExpression) {
        this.schedulerName = schedulerName;
        this.domain = domain;
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.status = status;
        this.cronExpression = cronExpression;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }
}
