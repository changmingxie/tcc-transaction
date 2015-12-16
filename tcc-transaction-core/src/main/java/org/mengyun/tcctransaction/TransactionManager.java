package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changmingxie on 10/26/15.
 */
public class TransactionManager {

    private TransactionConfigurator transactionConfigurator;

    public TransactionManager(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    private final Map<Thread, Transaction> transactionMap = new ConcurrentHashMap<Thread, Transaction>();

    public void begin() {

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.ROOT);

        transaction.setStatus(TransactionStatus.TRYING);
        this.transactionMap.put(Thread.currentThread(), transaction);


        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        transactionRepository.create(transaction);
    }

    public void propagationNewBegin(TransactionContext transactionContext) {

        Transaction transaction = new Transaction(transactionContext);
        transaction.setTransactionType(TransactionType.BRANCH);

        transaction.setStatus(TransactionStatus.TRYING);

        this.transactionMap.put(Thread.currentThread(), transaction);

        transactionConfigurator.getTransactionRepository().create(transaction);
    }

    public void propagationExistBegin(TransactionContext transactionContext) throws NoExistedTransactionException {
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        Transaction transaction = transactionRepository.findByXid(transactionContext.getXid());

        if (transaction != null) {
            transaction.setStatus(TransactionStatus.valueOf(transactionContext.getStatus()));

            this.transactionMap.put(Thread.currentThread(), transaction);
            transactionRepository.update(transaction);
        } else {
            throw new NoExistedTransactionException();
        }
    }


    public void commit() {

        Transaction transaction = getCurrentTransaction();

        transaction.setStatus(TransactionStatus.CONFIRMING);
        transactionConfigurator.getTransactionRepository().update(transaction);

        try {
            transaction.commit();
            transactionConfigurator.getTransactionRepository().delete(transaction);

        } catch (Throwable commitException) {

            if (transaction.getTransactionType().equals(TransactionType.ROOT)) {
                transactionConfigurator.getTransactionRepository().addErrorTransaction(transaction);
            }

            throw new RuntimeException(commitException);
        }
    }

    public Transaction getCurrentTransaction() {
        return transactionMap.get(Thread.currentThread());
    }

    public void rollback() {

        Transaction transaction = getCurrentTransaction();

        transaction.setStatus(TransactionStatus.CANCELLING);

        transactionConfigurator.getTransactionRepository().update(transaction);

        try {
            transaction.rollback();

            transactionConfigurator.getTransactionRepository().delete(transaction);
        } catch (Throwable rollbackException) {
            if (transaction.getTransactionType().equals(TransactionType.ROOT)) {
                transactionConfigurator.getTransactionRepository().addErrorTransaction(transaction);
            }
            throw new RuntimeException(rollbackException);
        }

    }
}
