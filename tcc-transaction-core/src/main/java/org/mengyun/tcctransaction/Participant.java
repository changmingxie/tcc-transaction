package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.*;

import java.io.Serializable;

/**
 * Created by changmingxie on 10/27/15.
 */
public class Participant implements Serializable {

    private static final long serialVersionUID = 4127729421281425247L;

    Class<? extends TransactionContextEditor> transactionContextEditorClass;

    private TransactionXid rootXid;


    private TransactionXid xid;
    private InvocationContext confirmInvocationContext;
    private InvocationContext cancelInvocationContext;
    private int status = ParticipantStatus.TRYING.getId();

    public Participant() {

    }

    public Participant(TransactionXid rootXid, TransactionXid xid, InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.xid = xid;
        this.rootXid = rootXid;
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public void rollback() {
        Terminator.invoke(new TransactionContext(rootXid, xid, TransactionStatus.CANCELLING.getId(), status), cancelInvocationContext, transactionContextEditorClass);
    }

    public void commit() {
        Terminator.invoke(new TransactionContext(rootXid, xid, TransactionStatus.CONFIRMING.getId(), status), confirmInvocationContext, transactionContextEditorClass);
    }

    public InvocationContext getConfirmInvocationContext() {
        return confirmInvocationContext;
    }

    public InvocationContext getCancelInvocationContext() {
        return cancelInvocationContext;
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status.getId();
    }

    public ParticipantStatus getStatus() {
        return ParticipantStatus.valueOf(this.status);
    }

    public TransactionXid getXid() {
        return xid;
    }

    public Class<? extends TransactionContextEditor> getTransactionContextEditorClass() {
        return transactionContextEditorClass;
    }

}
