package org.mengyun.tcctransaction.api;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changmingxie on 10/30/15.
 */
public class TransactionContext implements Serializable {

    private static final long serialVersionUID = -8199390103169700387L;
    private Xid xid;
    private Xid rootXid;
    private String rootDomain;

    private TransactionStatus status = TransactionStatus.TRYING;
    private ParticipantStatus participantStatus = ParticipantStatus.TRYING;
    private Map<String, String> attachments = new ConcurrentHashMap<>();

    public TransactionContext() {

    }

    public TransactionContext(String rootDomain, Xid rootXid, Xid xid, TransactionStatus status) {
        this(rootDomain, rootXid, xid, status, ParticipantStatus.TRYING);
    }

    public TransactionContext(String rootDomain, Xid rootXid, Xid xid, TransactionStatus status, ParticipantStatus participantStatus) {
        this.rootDomain = rootDomain;
        this.rootXid = rootXid;
        this.xid = xid;
        this.status = status;
        this.participantStatus = participantStatus;
    }

    public Xid getXid() {
        return xid;
    }

    public void setXid(Xid xid) {
        this.xid = xid;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            this.attachments.putAll(attachments);
        }
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

    public ParticipantStatus getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(ParticipantStatus participantStatus) {
        this.participantStatus = participantStatus;
    }

    public Xid getRootXid() {
        return rootXid;
    }

    public void setRootXid(Xid rootXid) {
        this.rootXid = rootXid;
    }
}
