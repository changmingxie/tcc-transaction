package org.mengyun.tcctransaction.transaction;

import org.mengyun.tcctransaction.api.ParticipantStatus;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.context.TransactionContextEditor;
import java.io.Serializable;

/**
 * Created by changmingxie on 10/27/15.
 */
public class Participant implements Serializable {

    private static final long serialVersionUID = 4127729421281425247L;

    Class<? extends TransactionContextEditor> transactionContextEditorClass;

    private Xid rootXid;

    private String rootDomain;

    private Xid xid;

    private InvocationContext invocationContext;

    private ParticipantStatus status = ParticipantStatus.TRYING;

    public Participant() {
    }

    public Participant(String rootDomain, Xid rootXid, Xid xid, InvocationContext invocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.xid = xid;
        this.rootXid = rootXid;
        this.rootDomain = rootDomain;
        this.invocationContext = invocationContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public void rollback() {
        Terminator.invoke(new TransactionContext(rootDomain, rootXid, xid, TransactionStatus.CANCELLING, status), new Invocation(invocationContext.getCancelMethodName(), invocationContext), transactionContextEditorClass);
    }

    public void commit() {
        Terminator.invoke(new TransactionContext(rootDomain, rootXid, xid, TransactionStatus.CONFIRMING, status), new Invocation(invocationContext.getConfirmMethodName(), invocationContext), transactionContextEditorClass);
    }

    public ParticipantStatus getStatus() {
        return this.status;
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status;
    }

    public Xid getXid() {
        return xid;
    }

    public void setXid(Xid xid) {
        this.xid = xid;
    }

    public Class<? extends TransactionContextEditor> getTransactionContextEditorClass() {
        return transactionContextEditorClass;
    }

    public void setTransactionContextEditorClass(Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.transactionContextEditorClass = transactionContextEditorClass;
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

    public InvocationContext getInvocationContext() {
        return invocationContext;
    }

    public void setInvocationContext(InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
    }
}
