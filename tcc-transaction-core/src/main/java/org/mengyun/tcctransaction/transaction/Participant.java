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
    private InvocationContext confirmInvocationContext;
    private InvocationContext cancelInvocationContext;
    private int status = ParticipantStatus.TRYING.getId();

    public Participant() {

    }

    public Participant(String rootDomain, Xid rootXid, Xid xid, InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.xid = xid;
        this.rootXid = rootXid;
        this.rootDomain = rootDomain;
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public void rollback() {
        Terminator.invoke(new TransactionContext(rootDomain, rootXid, xid, TransactionStatus.CANCELLING.getId(), status), cancelInvocationContext, transactionContextEditorClass);
    }

    public void commit() {
        Terminator.invoke(new TransactionContext(rootDomain, rootXid, xid, TransactionStatus.CONFIRMING.getId(), status), confirmInvocationContext, transactionContextEditorClass);
    }

    public InvocationContext getConfirmInvocationContext() {
        return confirmInvocationContext;
    }

    public InvocationContext getCancelInvocationContext() {
        return cancelInvocationContext;
    }

    public ParticipantStatus getStatus() {
        return ParticipantStatus.valueOf(this.status);
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status.getId();
    }

    public Xid getXid() {
        return xid;
    }

    public Class<? extends TransactionContextEditor> getTransactionContextEditorClass() {
        return transactionContextEditorClass;
    }

}
