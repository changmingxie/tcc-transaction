package org.mengyun.tcctransaction.transaction;


import org.mengyun.tcctransaction.api.ParticipantStatus;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.xid.TransactionXid;
import org.mengyun.tcctransaction.xid.UUIDGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changmingxie on 10/26/15.
 */
public class Transaction implements Serializable {

    private List<Participant> participants = new ArrayList<>();
    private Map<String, Object> attachments = new ConcurrentHashMap<>();
    private Date createTime = new Date();
    private Xid xid;
    private Xid rootXid;
    private String rootDomain;
    private TransactionType transactionType;
    private TransactionStatus status;
    private Date lastUpdateTime = new Date();
    private volatile int retriedCount = 0;
    private long version = 0L;

    /**
     * Use wrapper types for compatibility.
     */
    private Long id;

    public Transaction() {
    }

    public Transaction(TransactionContext transactionContext) {
        this.id = FactoryBuilder.factoryOf(UUIDGenerator.class).getInstance().generateLong();
        this.xid = transactionContext.getXid();
        this.rootXid = transactionContext.getRootXid();
        this.rootDomain = transactionContext.getRootDomain();
        this.status = TransactionStatus.TRYING;
        this.transactionType = TransactionType.BRANCH;
    }

    public Transaction(String rootDomain) {
        this(null, rootDomain);
    }

    public Transaction(Object uniqueIdentity, String rootDomain) {

        this.id = FactoryBuilder.factoryOf(UUIDGenerator.class).getInstance().generateLong();
        this.xid = TransactionXid.withUniqueIdentity(uniqueIdentity);
        this.status = TransactionStatus.TRYING;
        this.transactionType = TransactionType.ROOT;
        this.rootXid = xid;
        this.rootDomain = rootDomain;
    }

    public void enlistParticipant(Participant participant) {
        participants.add(participant);
    }

    public void commit() {
        for (Participant participant : participants) {
            if (!participant.getStatus().equals(ParticipantStatus.CONFIRM_SUCCESS)) {
                participant.commit();
                participant.setStatus(ParticipantStatus.CONFIRM_SUCCESS);
            }
        }
    }

    public void rollback() {
        for (Participant participant : participants) {
            if (!participant.getStatus().equals(ParticipantStatus.CANCEL_SUCCESS)) {
                participant.rollback();
                participant.setStatus(ParticipantStatus.CANCEL_SUCCESS);
            }
        }
    }

    public Xid getXid() {
        return xid;
    }

    public void setXid(Xid xid) {
        this.xid = xid;
    }

    public String getRootDomain() {
        return rootDomain;
    }

    public void setRootDomain(String rootDomain) {
        this.rootDomain = rootDomain;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date date) {
        this.lastUpdateTime = date;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Xid getRootXid() {
        return rootXid;
    }

    public void setRootXid(Xid rootXid) {
        this.rootXid = rootXid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
