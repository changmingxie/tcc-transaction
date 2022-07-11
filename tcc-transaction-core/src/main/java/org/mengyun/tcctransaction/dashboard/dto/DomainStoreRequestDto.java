package org.mengyun.tcctransaction.dashboard.dto;

/**
 * @Author huabao.fang
 * @Date 2022/6/14 14:17
 **/
public class DomainStoreRequestDto {

    private String domain;

    // 告警手机号列表,多个以英文逗号分割
    private String phoneNumbers;

    // 告警类型: DING-钉钉, SMS-短信, PHONE-电话
    private String alertType;

    //告警阈值
    private int threshold;

    // 告警间隔时间(单位为分钟) 避免频繁告警
    private int intervalMinutes;

    // 钉钉机器人地址
    private String dingRobotUrl;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(int intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public String getDingRobotUrl() {
        return dingRobotUrl;
    }

    public void setDingRobotUrl(String dingRobotUrl) {
        this.dingRobotUrl = dingRobotUrl;
    }
}
