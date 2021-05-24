package org.mengyun.tcctransaction.unittest.repository;

import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.CacheableTransactionRepository;
import org.mengyun.tcctransaction.repository.LocalStorable;
import org.mengyun.tcctransaction.repository.Page;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryStoreTransactionRepository extends CacheableTransactionRepository implements LocalStorable {

    String domain = "IN_MEMORY";

    private ConcurrentHashMap<Xid, Transaction> db = new ConcurrentHashMap<>();

    @Override
    protected int doCreate(Transaction transaction) {
        db.put(transaction.getXid(), transaction);
        return 1;
    }

    @Override
    protected int doUpdate(Transaction transaction) {
        db.put(transaction.getXid(), transaction);
        return 1;
    }

    @Override
    protected int doDelete(Transaction transaction) {
        db.remove(transaction.getXid());
        return 1;
    }

    @Override
    protected Transaction doFindOne(Xid xid) {
        return db.get(xid);
    }

    @Override
    protected Page<Transaction> doFindAllUnmodifiedSince(Date date, String offset, int pageSize) {
        return null;
    }

    @Override
    public String getDomain() {
        return domain;
    }
}
