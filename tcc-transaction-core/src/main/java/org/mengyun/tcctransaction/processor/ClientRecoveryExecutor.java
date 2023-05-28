package org.mengyun.tcctransaction.processor;

import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.recovery.RecoveryExecutor;
import org.mengyun.tcctransaction.repository.TransactionConvertor;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.mengyun.tcctransaction.serializer.json.FastjsonTransactionSerializer;
import org.mengyun.tcctransaction.storage.TransactionOptimisticLockException;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.support.FactoryBuilder;
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
        try {
            transactionRepository.update(transaction);
        } catch (TransactionOptimisticLockException e) {
            logger.debug("multiple instances try to recovery<rollback> the same transaction<{}>, this instance ignore the recovery.", transactionStore.getXid());
            return;
        }
        transaction.rollback();
        transactionRepository.delete(transaction);
    }

    @Override
    public void commit(TransactionStore transactionStore) {
        Transaction transaction = TransactionConvertor.getTransaction(transactionSerializer, transactionStore);
        transaction.setRetriedCount(transaction.getRetriedCount() + 1);
        transaction.setStatus(TransactionStatus.CONFIRMING);
        try {
            transactionRepository.update(transaction);
        } catch (TransactionOptimisticLockException e) {
            logger.debug("multiple instances try to recovery<commit> the same transaction<{}>, this instance ignore the recovery.", transactionStore.getXid());
            return;
        }
        transaction.commit();
        transactionRepository.delete(transaction);
    }

    @Override
    public byte[] transactionVisualize(String domain, byte[] content) {
        Transaction transaction = transactionSerializer.deserialize(content);
        FastjsonTransactionSerializer fastjsonTransactionSerializer = FactoryBuilder.factoryOf(FastjsonTransactionSerializer.class).getInstance();
        return fastjsonTransactionSerializer.serialize(transaction);
    }
}
