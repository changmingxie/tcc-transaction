package org.mengyun.tcctransaction.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.mengyun.tcctransaction.api.Xid;

/**
 * @Author huabao.fang
 * @Date 2022/5/25 14:14
 */
public class TransactionStoreDto {

    private String domain;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private Xid xid;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private Xid rootXid;

    private String rootDomain;

    private String content;

    private String createTime;

    private String lastUpdateTime;

    private long version;

    private int retriedCount;

    private int statusId;

    private int transactionTypeId;

    private String xidString;

    private String rootXidString;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Xid getXid() {
        return xid;
    }

    public void setXid(Xid xid) {
        this.xid = xid;
    }

    public Xid getRootXid() {
        return rootXid;
    }

    public void setRootXid(Xid rootXid) {
        this.rootXid = rootXid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getXidString() {
        return xidString;
    }

    public void setXidString(String xidString) {
        this.xidString = xidString;
    }

    public String getRootXidString() {
        return rootXidString;
    }

    public void setRootXidString(String rootXidString) {
        this.rootXidString = rootXidString;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getRootDomain() {
        return rootDomain;
    }

    public void setRootDomain(String rootDomain) {
        this.rootDomain = rootDomain;
    }
}
