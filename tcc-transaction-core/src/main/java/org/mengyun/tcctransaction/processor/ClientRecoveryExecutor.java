package org.mengyun.tcctransaction.processor;

import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.recovery.RecoveryExecutor;
import org.mengyun.tcctransaction.repository.TransactionConvertor;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.mengyun.tcctransaction.serializer.json.FastjsonTransactionSerializer;
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
        int result = transactionRepository.update(transaction);
        if (result > 0) {
            transaction.rollback();
            transactionRepository.delete(transaction);
        } else if (result == 0) {
            logger.debug("multiple instances try to recovery<rollback> the same transaction<" + transactionStore.getXid() + ", this instance ignore the recovery.");
        }
    }

    @Override
    public void commit(TransactionStore transactionStore) {

        Transaction transaction = TransactionConvertor.getTransaction(transactionSerializer, transactionStore);

        transaction.setRetriedCount(transaction.getRetriedCount() + 1);
        transaction.setStatus(TransactionStatus.CONFIRMING);
        int result = transactionRepository.update(transaction);
        if (result > 0) {
            transaction.commit();
            transactionRepository.delete(transaction);
        } else if (result == 0) {
            logger.debug("multiple instances try to recovery<commit> the same transaction<" + transactionStore.getXid() + ", this instance ignore the recovery.");
        }
    }

    @Override
    public byte[] transactionVisualize(String domain, byte[] content) {
        Transaction transaction = transactionSerializer.deserialize(content);
        FastjsonTransactionSerializer fastjsonTransactionSerializer = FactoryBuilder.factoryOf(FastjsonTransactionSerializer.class).getInstance();
        return fastjsonTransactionSerializer.serialize(transaction);
    }
}
