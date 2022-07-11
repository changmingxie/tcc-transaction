package org.mengyun.tcctransaction.dashboard.enums;


/**
 * @Author huabao.fang
 * @Date 2022/05/19 17:25
 **/
public enum ResponseCodeEnum {

    SUCCESS(200, "成功"),
    REQUEST_PARAM_ERROR(400, "参数错误"),
    UNKOWN_ERROR(500, "未知异常"),
    NOT_SUPPORT(405, "不支持"),
    NOT_SUPPORT_WITH_MESSAGE(405, "%s不支持"),

    // Domain管理相关
    DOMAIN_ILLEGAL_ALERT_TYPE(10100, "非法告警类型"),
    DOMAIN_NOT_EXIST(10101, "Domain记录不存在"),

    // 登录相关
    LOGIN_USER_NOT_EXST(10200, "用户不存在"),
    LOGIN_PASSWORD_ILLEGAL(10201, "密码有误"),
    LOGIN_ERROR(10209, "登录失败"),
    LOGIN_ERROR_WITH_MESSAGE(10209, "登录失败:%s"),

    // 任务管理相关
    TASK_OPERATE_NOT_SUPPORT(10300, "任务操作不支持"),
    TASK_STATUS_ERROR(10301, "任务状态异常"),
    TASK_MODIFY_CRON_ERROR(10302, "更新cron异常"),
    TASK_OPERATE_ERROR(10309, "任务操作异常"),

    // 告警相关
    ALERT_DING_ERROR(10400, "钉钉告警异常"),


    ;

    private int responseCode;
    private String responseMessage;

    ResponseCodeEnum(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage(Object... args) {
        try {
            return String.format(this.responseMessage, args);
        } catch (Exception e) {
            return this.responseMessage;
        }
    }

    public String getCode() {
        return String.valueOf(this.responseCode);
    }

    public String getMessage() {
        return this.responseMessage;
    }
}
