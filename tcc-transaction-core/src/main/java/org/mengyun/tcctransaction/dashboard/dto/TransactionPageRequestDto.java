package org.mengyun.tcctransaction.dashboard.dto;

/**
 * @Author huabao.fang
 * @Date 2022/5/21 08:21
 **/
public class TransactionPageRequestDto {

    private String domain;

    private String offset;

    private int pageSize;

    // true-查询标记已删除的事件  false-查询正常事件
    private boolean markDeleted;

    private String xidString;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isMarkDeleted() {
        return markDeleted;
    }

    public void setMarkDeleted(boolean markDeleted) {
        this.markDeleted = markDeleted;
    }

    public String getXidString() {
        return xidString;
    }

    public void setXidString(String xidString) {
        this.xidString = xidString;
    }
}
