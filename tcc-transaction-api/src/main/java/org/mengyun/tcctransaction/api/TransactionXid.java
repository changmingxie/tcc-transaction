package org.mengyun.tcctransaction.api;


import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by changmingxie on 10/26/15.
 */
public class TransactionXid implements Xid, Serializable {

    private static final long serialVersionUID = -6817267250789142043L;
    private int formatId = 1;

    private byte[] globalTransactionId;

    private byte[] branchQualifier;

    public TransactionXid() {
        globalTransactionId = UUID.randomUUID().toString().getBytes();
        branchQualifier = UUID.randomUUID().toString().getBytes();
    }

    public TransactionXid(byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
        branchQualifier = UUID.randomUUID().toString().getBytes();
    }

    public TransactionXid(byte[] globalTransactionId, byte[] branchQualifier) {
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }
    
    @Override
    public int getFormatId() {
        return formatId;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return globalTransactionId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return branchQualifier;
    }

    @Override
    public String toString() {

        return String.format("formatId:%d,globalTransactionId:%s,branchQualifier:%s",
                formatId, UUID.nameUUIDFromBytes(globalTransactionId).toString(),
                UUID.nameUUIDFromBytes(branchQualifier).toString()
        );
    }

    public TransactionXid clone() {


        byte[] cloneGlobalTransactionId = new byte[globalTransactionId.length];
        byte[] cloneBranchQualifier = new byte[branchQualifier.length];

        System.arraycopy(globalTransactionId, 0, cloneGlobalTransactionId, 0, globalTransactionId.length);
        System.arraycopy(branchQualifier, 0, cloneBranchQualifier, 0, branchQualifier.length);

        TransactionXid clone = new TransactionXid(cloneGlobalTransactionId, cloneBranchQualifier);
        return clone;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getFormatId();
        result = prime * result + Arrays.hashCode(branchQualifier);
        result = prime * result + Arrays.hashCode(globalTransactionId);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        TransactionXid other = (TransactionXid) obj;
        if (this.getFormatId() != other.getFormatId()) {
            return false;
        } else if (Arrays.equals(branchQualifier, other.branchQualifier) == false) {
            return false;
        } else if (Arrays.equals(globalTransactionId, other.globalTransactionId) == false) {
            return false;
        }
        return true;
    }
}


