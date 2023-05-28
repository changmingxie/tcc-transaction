package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.transaction.Transaction;

public final class TransactionConvertor {

    private TransactionConvertor() {
    }

    public static Transaction getTransaction(TransactionSerializer serializer, TransactionStore transactionStore) {
        Transaction transaction = serializer.deserialize(transactionStore.getContent());
        transaction.setStatus(TransactionStatus.valueOf(transactionStore.getStatusId()));
        transaction.setVersion(transactionStore.getVersion());
        transaction.setLastUpdateTime(transactionStore.getLastUpdateTime());
        transaction.setRetriedCount(transactionStore.getRetriedCount());
        return transaction;
    }

    public static TransactionStore getTransactionStore(TransactionSerializer serializer, String domain, Transaction transaction) {
        TransactionStore transactionStore = new TransactionStore();
        transactionStore.setXid(transaction.getXid());
        transactionStore.setRootXid(transaction.getRootXid());
        transactionStore.setRootDomain(transaction.getRootDomain());
        transactionStore.setContent(serializer.serialize(transaction));
        transactionStore.setStatusId(transaction.getStatus().getId());
        transactionStore.setVersion(transaction.getVersion());
        transactionStore.setLastUpdateTime(transaction.getLastUpdateTime());
        transactionStore.setRetriedCount(transaction.getRetriedCount());
        transactionStore.setCreateTime(transaction.getCreateTime());
        transactionStore.setDomain(domain);
        transactionStore.setTransactionTypeId(transaction.getTransactionType().getId());
        return transactionStore;
    }
}
