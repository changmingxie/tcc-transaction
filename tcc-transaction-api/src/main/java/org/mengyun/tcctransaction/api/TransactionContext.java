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

    private int status = TransactionStatus.TRYING.getId();
    private int participantStatus = ParticipantStatus.TRYING.getId();
    private Map<String, String> attachments = new ConcurrentHashMap<String, String>();

    public TransactionContext() {

    }

    public TransactionContext(String rootDomain, Xid rootXid, Xid xid, int status) {
        this(rootDomain, rootXid, xid, status, ParticipantStatus.TRYING.getId());
    }

    public TransactionContext(String rootDomain, Xid rootXid, Xid xid, int status, int participantStatus) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(int participantStatus) {
        this.participantStatus = participantStatus;
    }

    public Xid getRootXid() {
        return rootXid;
    }

    public void setRootXid(Xid rootXid) {
        this.rootXid = rootXid;
    }
}
