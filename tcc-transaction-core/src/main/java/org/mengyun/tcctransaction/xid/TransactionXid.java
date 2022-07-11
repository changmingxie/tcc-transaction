package org.mengyun.tcctransaction.xid;


import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.io.Serializable;

/**
 * Created by changmingxie on 10/26/15.
 */
public class TransactionXid implements Xid, Serializable {

    private String xid;

    public static TransactionXid withUniqueIdentity(Object uniqueIdentity) {
        String xid = null;
        if (uniqueIdentity == null) {
            xid = FactoryBuilder.factoryOf(UUIDGenerator.class).getInstance().generate();
        } else {
            xid = uniqueIdentity.toString();
        }
        return new TransactionXid(xid);
    }

    public static TransactionXid withUuid() {
        return new TransactionXid(FactoryBuilder.factoryOf(UUIDGenerator.class).getInstance().generate());
    }

    public TransactionXid() {

    }

    public TransactionXid(String xidString) {
        this.xid = xidString;
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
    public String getXid() {
        return xid;
    }

}


