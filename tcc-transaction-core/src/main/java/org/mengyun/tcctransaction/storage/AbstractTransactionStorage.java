package org.mengyun.tcctransaction.storage;


import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;

/**
 * Created by changmingxie on 10/30/15.
 */
public abstract class AbstractTransactionStorage implements TransactionStorage, AutoCloseable {

    protected TransactionStoreSerializer serializer;

    protected StoreConfig storeConfig;

    public AbstractTransactionStorage(TransactionStoreSerializer serializer, StoreConfig storeConfig) {
        this.serializer = serializer;
        this.storeConfig = storeConfig;
    }

    @Override
    public int create(TransactionStore transactionStore) {
        if (transactionStore.getContent().length > this.storeConfig.getMaxTransactionSize()) {
            throw new TransactionIOException("the size of transaction is more bigger than " + this.storeConfig.getMaxTransactionSize());
        }
        transactionStore.setVersion(1l);
        int result = doCreate(transactionStore);
        if (result < 0) {
            throw new TransactionIOException(transactionStore.simpleDetail());
        }
        return result;
    }

    @Override
    public int update(TransactionStore transactionStore) {
        int result = 0;

        result = doUpdate(transactionStore);
        if (result <= 0) {
            throw new TransactionOptimisticLockException(transactionStore.simpleDetail());
        }

        return result;
    }

    @Override
    public int delete(TransactionStore transactionStore) {
        return doDelete(transactionStore);
    }

    @Override
    public TransactionStore findByXid(String domain, Xid transactionXid) {
        TransactionStore transactionStore = doFindOne(domain, transactionXid, false);
        return transactionStore;
    }

    @Override
    public TransactionStore findMarkDeletedByXid(String domain, Xid transactionXid) {
        TransactionStore transactionStore = doFindOne(domain, transactionXid, true);
        return transactionStore;
    }

    @Override
    public int markDeleted(TransactionStore transactionStore) {
        return doMarkDeleted(transactionStore);
    }

    @Override
    public int restore(TransactionStore transactionStore) {
        return doRestore(transactionStore);
    }

    protected abstract int doCreate(TransactionStore transactionStore);

    protected abstract int doUpdate(TransactionStore transactionStore);

    protected abstract int doDelete(TransactionStore transactionStore);

    protected abstract int doMarkDeleted(TransactionStore transactionStore);

    protected abstract int doRestore(TransactionStore transactionStore);

    protected abstract TransactionStore doFindOne(String domain, Xid xid, boolean isMarkDeleted);

}
