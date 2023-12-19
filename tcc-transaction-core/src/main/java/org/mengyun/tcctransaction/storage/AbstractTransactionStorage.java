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
            throw new TransactionIOException(String.format("cur transaction size(%dB) is bigger than maxTransactionSize(%dB), consider to reduce parameter size or adjust maxTransactionSize", transactionStore.getContent().length, this.storeConfig.getMaxTransactionSize()));
        }

        int result = doCreate(transactionStore);

        if (result > 0) {
            return result;
        } else {

            TransactionStore foundTransactionStore = findByXid(transactionStore.getDomain(), transactionStore.getXid());

            if (foundTransactionStore != null && transactionStore.getRequestId() != null
                    && transactionStore.getRequestId().equals(foundTransactionStore.getRequestId())
                    && transactionStore.getVersion() == foundTransactionStore.getVersion()
                    && (transactionStore.getId() == null || transactionStore.getId().equals(foundTransactionStore.getId()))) {
                return 1;
            }

            throw new TransactionIOException(transactionStore.simpleDetail());
        }
    }

    @Override
    public int update(TransactionStore transactionStore) {
        int result = doUpdate(transactionStore);

        if (result > 0) {
            return result;
        } else {
            //compare the content except the version
            TransactionStore foundTransactionStore = findByXid(transactionStore.getDomain(), transactionStore.getXid());

            if (foundTransactionStore != null && transactionStore.getRequestId() != null
                    && transactionStore.getRequestId().equals(foundTransactionStore.getRequestId())
                    && transactionStore.getVersion() == foundTransactionStore.getVersion()
                    && (transactionStore.getId() == null || transactionStore.getId().equals(foundTransactionStore.getId()))) {
                return 1;
            }

            throw new TransactionOptimisticLockException(transactionStore.simpleDetail());
        }
    }

    @Override
    public int delete(TransactionStore transactionStore) {
        return doDelete(transactionStore);
    }

    @Override
    public TransactionStore findByXid(String domain, Xid transactionXid) {
        return doFindOne(domain, transactionXid, false);
    }

    @Override
    public TransactionStore findMarkDeletedByXid(String domain, Xid transactionXid) {
        return doFindOne(domain, transactionXid, true);
    }

    @Override
    public int markDeleted(TransactionStore transactionStore) {
        return doMarkDeleted(transactionStore);
    }

    @Override
    public int completelyDelete(TransactionStore transactionStore) {
        return doCompletelyDelete(transactionStore);
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

    protected abstract int doCompletelyDelete(TransactionStore transactionStore);

    protected abstract TransactionStore doFindOne(String domain, Xid xid, boolean isMarkDeleted);

}
