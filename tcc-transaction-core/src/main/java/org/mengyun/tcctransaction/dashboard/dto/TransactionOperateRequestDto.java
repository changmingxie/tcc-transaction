package org.mengyun.tcctransaction.dashboard.dto;

/**
 * @Author huabao.fang
 * @Date 2022/5/19 20:30
 **/
public class TransactionOperateRequestDto {
    private String domain;
    private String xidString;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getXidString() {
        return xidString;
    }

    public void setXidString(String xidString) {
        this.xidString = xidString;
    }
}
