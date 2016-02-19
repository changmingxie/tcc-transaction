package org.mengyun.tcctransaction.spring.repository;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.api.TransactionXid;
import org.springframework.util.CollectionUtils;

import javax.transaction.xa.Xid;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by changmingxie on 10/30/15.
 */
public abstract class CachableTransactionRepository implements TransactionRepository {

    private int expireDurationInSeconds = 300;

    private int errorExpireDurationInSeconds = 300;

    private Cache<Xid, Transaction> transactionXidCompensableTransactionCache;

    private Cache<Xid, Transaction> errorTransactionXidCompensableTransactionCache;

    @Override
    public void create(Transaction transaction) {
        doCreate(transaction);
        putToCache(transaction);
    }

    @Override
    public void update(Transaction transaction) {
        doUpdate(transaction);
        putToCache(transaction);
    }

    @Override
    public void delete(Transaction transaction) {
        doDelete(transaction);
        removeFromCache(transaction);
    }

    @Override
    public Transaction findByXid(TransactionXid transactionXid) {
        Transaction transaction = findFromCache(transactionXid);

        if (transaction == null) {
            transaction = doFind(transactionXid);

            if (transaction != null) {
                putToCache(transaction);
            }
        }

        return transaction;
    }

    @Override
    public List<Transaction> findAll() {

        List<Transaction> transactions = doFindAll();
        for (Transaction transaction : transactions) {
            putToCache(transaction);
        }

        return transactions;
    }

    @Override
    public void addErrorTransaction(Transaction transaction) {
        putToErrorCache(transaction);
    }

    @Override
    public void removeErrorTransaction(Transaction transaction) {
        removeFromErrorCache(transaction);
    }

    @Override
    public Collection<Transaction> findAllErrorTransactions() {
        return findAllFromErrorCache();
    }

    public CachableTransactionRepository() {
        transactionXidCompensableTransactionCache = CacheBuilder.newBuilder().expireAfterAccess(expireDurationInSeconds, TimeUnit.SECONDS).maximumSize(1000).build();
        errorTransactionXidCompensableTransactionCache = CacheBuilder.newBuilder().expireAfterAccess(errorExpireDurationInSeconds, TimeUnit.SECONDS).maximumSize(1000).build();
    }

    protected void putToCache(Transaction transaction) {
        transactionXidCompensableTransactionCache.put(transaction.getXid(), transaction);
    }

    protected void removeFromCache(Transaction transaction) {
        transactionXidCompensableTransactionCache.invalidate(transaction.getXid());
    }

    protected Transaction findFromCache(TransactionXid transactionXid) {
        return transactionXidCompensableTransactionCache.getIfPresent(transactionXid);
    }

    protected void putToErrorCache(Transaction transaction) {
        errorTransactionXidCompensableTransactionCache.put(transaction.getXid(), transaction);
    }

    protected void removeFromErrorCache(Transaction transaction) {
        errorTransactionXidCompensableTransactionCache.invalidate(transaction.getXid());
    }

    protected Collection<Transaction> findAllFromErrorCache() {
        return errorTransactionXidCompensableTransactionCache.asMap().values();
    }

    public final void setExpireDurationInSeconds(int durationInSeconds) {
        this.expireDurationInSeconds = durationInSeconds;
    }

    public final void setErrorExpireDurationInSeconds(int durationInSeconds) {
        this.errorExpireDurationInSeconds = durationInSeconds;
    }

    protected Transaction doFind(TransactionXid xid) {

        List<TransactionXid> transactionXids = Arrays.asList(xid);

        List<Transaction> transactions = doFindAll(transactionXids);

        if (!CollectionUtils.isEmpty(transactions)) {
            return transactions.get(0);
        }
        return null;
    }

    protected abstract void doCreate(Transaction transaction);

    protected abstract void doUpdate(Transaction transaction);

    protected abstract void doDelete(Transaction transaction);

    protected abstract List<Transaction> doFindAll(List<TransactionXid> xids);

    protected abstract List<Transaction> doFindAll();
}
