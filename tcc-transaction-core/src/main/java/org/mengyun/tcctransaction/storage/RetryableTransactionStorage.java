package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * @author Nervose.Wu
 * @date 2022/6/30 12:58
 */
public class RetryableTransactionStorage implements TransactionStorage, StorageRecoverable {

    static final Logger logger = LoggerFactory.getLogger(RetryableTransactionStorage.class.getSimpleName());

    private int maxAttempts;
    private TransactionStorage transactionStorage;

    public RetryableTransactionStorage(int maxAttempts, TransactionStorage transactionStorage) {
        this.maxAttempts = maxAttempts;
        this.transactionStorage = transactionStorage;
    }

    @Override
    public int create(TransactionStore transactionStore) {
        return doWithRetry(() -> transactionStorage.create(transactionStore));
    }

    @Override
    public int update(TransactionStore transactionStore) {
        return doWithRetry(() -> transactionStorage.update(transactionStore));
    }

    @Override
    public int delete(TransactionStore transactionStore) {
        return doWithRetry(() -> transactionStorage.delete(transactionStore));
    }

    @Override
    public TransactionStore findByXid(String domain, Xid xid) {
        return doWithRetry(() -> transactionStorage.findByXid(domain, xid));
    }

    @Override
    public TransactionStore findMarkDeletedByXid(String domain, Xid xid) {
        return doWithRetry(() -> transactionStorage.findMarkDeletedByXid(domain, xid));
    }

    @Override
    public int markDeleted(TransactionStore transactionStore) {
        return doWithRetry(() -> transactionStorage.markDeleted(transactionStore));
    }

    @Override
    public int restore(TransactionStore transactionStore) {
        return doWithRetry(() -> transactionStorage.restore(transactionStore));
    }

    @Override
    public int completelyDelete(TransactionStore transactionStore) {
        return doWithRetry(() -> transactionStorage.completelyDelete(transactionStore));
    }

    @Override
    public boolean supportStorageRecoverable() {
        return transactionStorage.supportStorageRecoverable();
    }

    private int doWithRetry(IntSupplier supplier) {
        int curAttempts = 0;
        do {
            curAttempts++;
            try {
                return supplier.getAsInt();
            } catch (Exception e) {
                logger.debug("current attempt: {} failed", curAttempts, e);
                if (curAttempts >= maxAttempts) {
                    throw e;
                }
            }
        } while (true);
    }

    private <T> T doWithRetry(Supplier<T> supplier) {
        int curAttempts = 0;
        do {
            curAttempts++;
            try {
                return supplier.get();
            } catch (Exception e) {
                logger.debug("current attempt: {} failed", curAttempts, e);
                if (curAttempts >= maxAttempts) {
                    throw e;
                }
            }
        } while (true);
    }

    @Override
    public void close() {
        transactionStorage.close();
    }

    @Override
    public Page<TransactionStore> findAllUnmodifiedSince(String domain, Date date, String offset, int pageSize) {
        return getStorageRecoverable().findAllUnmodifiedSince(domain, date, offset, pageSize);
    }

    @Override
    public Page<TransactionStore> findAllDeletedSince(String domain, Date date, String offset, int pageSize) {
        return getStorageRecoverable().findAllDeletedSince(domain, date, offset, pageSize);
    }

    @Override
    public int count(String domain, boolean isMarkDeleted) {
        return getStorageRecoverable().count(domain, isMarkDeleted);
    }

    @Override
    public void registerDomain(DomainStore domainStore) {
        getStorageRecoverable().registerDomain(domainStore);
    }

    @Override
    public void updateDomain(DomainStore domainStore) {
        getStorageRecoverable().updateDomain(domainStore);
    }

    @Override
    public void removeDomain(String domain) {
        getStorageRecoverable().removeDomain(domain);
    }

    @Override
    public DomainStore findDomain(String domain) {
        return getStorageRecoverable().findDomain(domain);
    }

    @Override
    public List<DomainStore> getAllDomains() {
        return getStorageRecoverable().getAllDomains();
    }

    private StorageRecoverable getStorageRecoverable() {
        if (transactionStorage.supportStorageRecoverable() && transactionStorage instanceof StorageRecoverable) {
            return (StorageRecoverable) transactionStorage;
        }
        throw new UnsupportedOperationException(String.format("%s not support StorageRecoverable", transactionStorage.getClass()));
    }

    public TransactionStorage getTargetTransactionStorage() {
        return this.transactionStorage;
    }
}
