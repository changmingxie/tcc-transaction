package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.api.Xid;

import java.util.Date;
import java.util.Objects;

public class TransactionStore {
    private String domain;
    private String rootDomain;
    private Xid xid;
    private Xid rootXid;
    private byte[] content;
    private Date createTime = new Date();
    private Date lastUpdateTime = new Date();
    private long version = 0l;
    private int retriedCount = 0;
    private int statusId;
    private int transactionTypeId;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Xid getXid() {
        return xid;
    }

    public void setXid(Xid xid) {
        this.xid = xid;
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

    public int getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Xid getRootXid() {
        return rootXid;
    }

    public void setRootXid(Xid rootXid) {
        this.rootXid = rootXid;
    }

    public String getRootDomain() {
        return rootDomain;
    }

    public void setRootDomain(String rootDomain) {
        this.rootDomain = rootDomain;
    }

    public String simpleDetail() {
        return "{" +
                "domain='" + domain + '\'' +
                ", rootDomain='" + rootDomain + '\'' +
                ", xid=" + xid +
                ", rootXid=" + rootXid +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", version=" + version +
                ", retriedCount=" + retriedCount +
                ", statusId=" + statusId +
                ", transactionTypeId=" + transactionTypeId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TransactionStore)) {
            return false;
        }

        TransactionStore other = (TransactionStore) obj;

        if (Objects.equals(this.domain, other.domain)
                && Objects.equals(this.rootDomain, other.rootDomain)
                && (Objects.equals(this.xid, other.xid))
                && (Objects.equals(this.rootXid, other.rootXid))
                && (Objects.deepEquals(this.content, other.content))
                && (Objects.equals(this.createTime, other.createTime))
                && (Objects.equals(this.lastUpdateTime, other.lastUpdateTime))
                && this.version == other.version
                && this.retriedCount == other.retriedCount
                && this.statusId == other.statusId
                && this.transactionTypeId == other.transactionTypeId
        ) {
            return true;
        }
        return false;
    }
}
