package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.Transaction;

import javax.transaction.xa.Xid;
import java.util.Date;

public class NoStoreTransactionRepository extends CacheableTransactionRepository implements LocalStorable {

    public NoStoreTransactionRepository() {
    }

    @Override
    protected int doCreate(Transaction transaction) {
        return 1;
    }

    @Override
    protected int doUpdate(Transaction transaction) {
        return 1;
    }

    @Override
    protected int doDelete(Transaction transaction) {
        return 1;
    }

    @Override
    protected Transaction doFindOne(Xid xid) {
        return null;
    }

    @Override
    protected Page<Transaction> doFindAllUnmodifiedSince(Date date, String offset, int pageSize) {
        return new Page<>();
    }

    @Override
    public String getDomain() {
        return null;
    }
}
