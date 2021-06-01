package org.mengyun.tcctransaction.repository;


import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionOptimisticLockException;

import javax.transaction.xa.Xid;
import java.util.Date;

/**
 * Created by changmingxie on 10/30/15.
 */
public abstract class AbstractTransactionRepository implements TransactionRepository, AutoCloseable {

    public AbstractTransactionRepository() {
    }

    @Override
    public int create(Transaction transaction) {
        transaction.setVersion(1l);
        int result = doCreate(transaction);
        return result;
    }

    @Override
    public int update(Transaction transaction) {
        int result = 0;

        result = doUpdate(transaction);
        if (result <= 0) {
            throw new TransactionOptimisticLockException();
        }

        return result;
    }

    @Override
    public int delete(Transaction transaction) {
        return doDelete(transaction);
    }

    @Override
    public Transaction findByXid(Xid transactionXid) {

        Transaction transaction = doFindOne(transactionXid);

        return transaction;
    }

    @Override
    public Page<Transaction> findAllUnmodifiedSince(Date date, String offset, int pageSize) {

        Page<Transaction> page = doFindAllUnmodifiedSince(date, offset, pageSize);

        return page;
    }

    protected abstract int doCreate(Transaction transaction);

    protected abstract int doUpdate(Transaction transaction);

    protected abstract int doDelete(Transaction transaction);

    protected abstract Transaction doFindOne(Xid xid);

    protected abstract Page<Transaction> doFindAllUnmodifiedSince(Date date, String offset, int pageSize);

}
