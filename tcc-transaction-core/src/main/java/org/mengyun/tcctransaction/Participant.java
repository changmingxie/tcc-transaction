package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.TransactionXid;

import java.io.Serializable;

/**
 * Created by changmingxie on 10/27/15.
 */
public class Participant implements Serializable {

    private TransactionXid xid;

    private Terminator terminator;

    public Participant(TransactionXid xid, Terminator terminator) {
        this.xid = xid;
        this.terminator = terminator;
    }

    public void rollback() {
        terminator.rollback();
    }

    public void commit() {
        terminator.commit();
    }

}
