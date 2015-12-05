package org.mengyun.tcctransaction;


import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.TransactionXid;

import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by changmingxie on 10/26/15.
 */
public class Transaction implements Serializable {

    private TransactionXid xid;

    private TransactionStatus status;

    private TransactionType transactionType;

    private List<Participant> participants = new ArrayList<Participant>();

    public Transaction() {
        this.xid = new TransactionXid();
    }

    public Transaction(TransactionContext transactionContext) {
        this.xid = transactionContext.getXid();
        this.status = TransactionStatus.valueOf(transactionContext.getStatus());
    }

    public Transaction(TransactionXid xid, TransactionStatus status, TransactionType transactionType, List<Participant> participants) {
        this.xid = xid;
        this.status = status;
        this.transactionType = transactionType;
        this.participants.addAll(participants);
    }

    public Xid getXid() {
        return xid.clone();
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void enlistParticipant(Participant participant) {
        participants.add(participant);
    }

    public List<Participant> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void commit() {

        for (Participant participant : participants) {
            participant.commit();
        }
    }

    public void rollback() {
        for (Participant participant : participants) {
            participant.rollback();
        }
    }
}
