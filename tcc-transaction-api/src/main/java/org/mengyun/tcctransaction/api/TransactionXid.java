package org.mengyun.tcctransaction.api;


import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.nio.ByteBuffer;
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
        globalTransactionId = uuidToByteArray(UUID.randomUUID());
        branchQualifier = uuidToByteArray(UUID.randomUUID());
    }

    public void setGlobalTransactionId(byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
    }

    public void setBranchQualifier(byte[] branchQualifier) {
        this.branchQualifier = branchQualifier;
    }

    public TransactionXid(byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
        branchQualifier = uuidToByteArray(UUID.randomUUID());
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

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UUID.nameUUIDFromBytes(globalTransactionId).toString());
        stringBuilder.append(":").append(UUID.nameUUIDFromBytes(branchQualifier).toString());

        return stringBuilder.toString();
    }

    public TransactionXid clone() {

        byte[] cloneGlobalTransactionId = null;
        byte[] cloneBranchQualifier = null;

        if (globalTransactionId != null) {
            cloneGlobalTransactionId = new byte[globalTransactionId.length];
            System.arraycopy(globalTransactionId, 0, cloneGlobalTransactionId, 0, globalTransactionId.length);
        }

        if (branchQualifier != null) {
            cloneBranchQualifier = new byte[branchQualifier.length];
            System.arraycopy(branchQualifier, 0, cloneBranchQualifier, 0, branchQualifier.length);
        }

        return new TransactionXid(cloneGlobalTransactionId, cloneBranchQualifier);
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
        } else if (!Arrays.equals(branchQualifier, other.branchQualifier)) {
            return false;
        } else if (!Arrays.equals(globalTransactionId, other.globalTransactionId)) {
            return false;
        }
        return true;
    }

    private static byte[] uuidToByteArray(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private static UUID byteArrayToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }
}


