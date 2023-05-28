package org.mengyun.tcctransaction.storage.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author huabao.fang
 * @Date 2022/6/13 22:48
 */
public class DomainStore implements Serializable {

    private static final long serialVersionUID = -7979140711432461167L;

    private String domain;

    // 告警手机号列表,多个以英文逗号分割
    private String phoneNumbers;

    // 告警类型: DING-钉钉, SMS-短信, PHONE-电话
    private AlertType alertType;

    //告警阈值
    private int threshold;

    // 告警间隔时间(单位为分钟) 避免频繁告警
    private int intervalMinutes;

    // 上次告警时间
    private Date lastAlertTime;

    // 钉钉机器人地址
    private String dingRobotUrl;

    private Date createTime = new Date();

    private Date lastUpdateTime = new Date();

    private long version = 0L;

    public DomainStore() {
    }

    public DomainStore(String domain) {
        this.domain = domain;
        //设置默认告警类型
        this.alertType = AlertType.DING;
    }

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

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getLastAlertTime() {
        return lastAlertTime;
    }

    public void setLastAlertTime(Date lastAlertTime) {
        this.lastAlertTime = lastAlertTime;
    }
}
