package org.mengyun.tcctransaction.processor;

import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.recovery.RecoveryExecutor;
import org.mengyun.tcctransaction.repository.TransactionConvertor;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRecoveryExecutor implements RecoveryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ClientRecoveryExecutor.class.getSimpleName());

    private TransactionRepository transactionRepository;
    private TransactionSerializer transactionSerializer;

    public ClientRecoveryExecutor(TransactionSerializer transactionSerializer, TransactionRepository transactionRepository) {
        this.transactionSerializer = transactionSerializer;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void rollback(TransactionStore transactionStore) {

        Transaction transaction = TransactionConvertor.getTransaction(transactionSerializer, transactionStore);

        transaction.setRetriedCount(transaction.getRetriedCount() + 1);
        transaction.setStatus(TransactionStatus.CANCELLING);
        transactionRepository.update(transaction);
        transaction.rollback();
        transactionRepository.delete(transaction);
    }

    @Override
    public void commit(TransactionStore transactionStore) {

        Transaction transaction = TransactionConvertor.getTransaction(transactionSerializer, transactionStore);

        transaction.setRetriedCount(transaction.getRetriedCount() + 1);
        transaction.setStatus(TransactionStatus.CONFIRMING);
        transactionRepository.update(transaction);
        transaction.commit();
        transactionRepository.delete(transaction);
    }
}
