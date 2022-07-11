package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.mengyun.tcctransaction.storage.Page;
import org.mengyun.tcctransaction.storage.StorageRecoverable;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.transaction.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DefaultTransactionRepository implements TransactionRepository {

    private String domain;

    private TransactionStorage transactionStorage;

    private TransactionSerializer serializer;

    public DefaultTransactionRepository(String domain, TransactionSerializer serializer, TransactionStorage transactionStorage) {
        this.transactionStorage = transactionStorage;
        this.serializer = serializer;
        this.domain = domain;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public int create(Transaction transaction) {
        TransactionStore transactionStore = getTransactionStore(transaction);
        int result = this.transactionStorage.create(transactionStore);
        transaction.setVersion(1l);
        return result;
    }

    @Override
    public int update(Transaction transaction) {
        TransactionStore transactionStore = getTransactionStore(transaction);
        int result = this.transactionStorage.update(transactionStore);
        transaction.setVersion(transaction.getVersion() + 1);
        transaction.setLastUpdateTime(new Date());
        return result;
    }

    @Override
    public int delete(Transaction transaction) {
        TransactionStore transactionStore = getTransactionStore(transaction);
        return this.transactionStorage.delete(transactionStore);
    }

    @Override
    public Transaction findByXid(Xid xid) {
        TransactionStore transactionStore = this.transactionStorage.findByXid(this.domain, xid);
        if (transactionStore != null) {
            return getTransaction(transactionStore);
        }
        return null;
    }

    @Override
    public boolean supportRecovery() {
        return this.transactionStorage.supportStorageRecoverable();
    }

    @Override
    public Page<Transaction> findAllUnmodifiedSince(Date date, String offset, int pageSize) {
        if (this.transactionStorage.supportStorageRecoverable()) {
            Page<TransactionStore> transactionStorePage = ((StorageRecoverable) this.transactionStorage).findAllUnmodifiedSince(this.domain, date, offset, pageSize);

            return getTransactionPage(transactionStorePage);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        this.transactionStorage.close();
    }

    private Page<Transaction> getTransactionPage(Page<TransactionStore> transactionStorePage) {
        Page<Transaction> page = new Page<Transaction>();
        page.setNextOffset(transactionStorePage.getNextOffset());
        page.setAttachment(transactionStorePage.getAttachment());
        page.setData(getTransactions(transactionStorePage.getData()));
        return page;
    }

    private List<Transaction> getTransactions(List<TransactionStore> transactionStores) {
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionStore transactionStore : transactionStores) {
            transactions.add(getTransaction(transactionStore));
        }
        return transactions;
    }

    public Transaction getTransaction(TransactionStore transactionStore) {
        return TransactionConvertor.getTransaction(serializer, transactionStore);
    }

    public TransactionStore getTransactionStore(Transaction transaction) {
        return TransactionConvertor.getTransactionStore(serializer, this.domain, transaction);
    }
}
