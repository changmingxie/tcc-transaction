package org.mengyun.tcctransaction.xid;


import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.io.Serializable;

/**
 * Created by changmingxie on 10/26/15.
 */
public class TransactionXid implements Xid, Serializable {

    private int formatId = Xid.AUTO;
    private String xid;

    public TransactionXid() {

    }

    public TransactionXid(String xidString) {
        this.xid = xidString;
    }

    public TransactionXid(int formatId, String xidString) {
        this.formatId = formatId;
        this.xid = xidString;
    }

    public static TransactionXid withUniqueIdentity(Object uniqueIdentity) {
        int formatId = Xid.AUTO;
        String xid = null;
        if (uniqueIdentity == null) {
            xid = FactoryBuilder.factoryOf(UUIDGenerator.class).getInstance().generateString();
        } else {
            xid = uniqueIdentity.toString();
            formatId = Xid.CUSTOMIZED;
        }
        return new TransactionXid(formatId, xid);
    }

    public static TransactionXid withUuid() {
        return new TransactionXid(FactoryBuilder.factoryOf(UUIDGenerator.class).getInstance().generateString());
    }

    @Override
    public String toString() {
        return this.xid;
    }

    public int hashCode() {
        if (this.xid == null) {
            return 0;
        }
        return this.xid.hashCode();
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
        return this.xid.equals(other.xid);
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    @Override
    public String getXid() {
        return xid;
    }

}


