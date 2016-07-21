package org.mengyun.tcctransaction;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.support.TransactionConfigurator;

/**
 * Created by changmingxie on 10/26/15.
 */
public class TransactionManager {


    static final Logger logger = Logger.getLogger(TransactionManager.class.getSimpleName());

    private TransactionConfigurator transactionConfigurator;

    public TransactionManager(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    private ThreadLocal<Transaction> threadLocalTransaction = new ThreadLocal<Transaction>();

    public void begin() {

        Transaction transaction = new Transaction(TransactionType.ROOT);
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        transactionRepository.create(transaction);
        threadLocalTransaction.set(transaction);
    }

    public void propagationNewBegin(TransactionContext transactionContext) {

        Transaction transaction = new Transaction(transactionContext);
        transactionConfigurator.getTransactionRepository().create(transaction);

        threadLocalTransaction.set(transaction);
    }

    public void propagationExistBegin(TransactionContext transactionContext) throws NoExistedTransactionException {
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        Transaction transaction = transactionRepository.findByXid(transactionContext.getXid());

        if (transaction != null) {
            transaction.changeStatus(TransactionStatus.valueOf(transactionContext.getStatus()));
            threadLocalTransaction.set(transaction);
        } else {
            throw new NoExistedTransactionException();
        }
    }

    public void commit() {

        Transaction transaction = getCurrentTransaction();

        transaction.changeStatus(TransactionStatus.CONFIRMING);

        transactionConfigurator.getTransactionRepository().update(transaction);

        try {
            transaction.commit();
            transactionConfigurator.getTransactionRepository().delete(transaction);
        } catch (Throwable commitException) {
            logger.error("compensable transaction confirm failed.", commitException);
            throw new ConfirmingException(commitException);
        }
    }

    public Transaction getCurrentTransaction() {
        return threadLocalTransaction.get();
    }

    public void rollback() {

        Transaction transaction = getCurrentTransaction();
        transaction.changeStatus(TransactionStatus.CANCELLING);

        transactionConfigurator.getTransactionRepository().update(transaction);
        
        try {
            transaction.rollback();
            transactionConfigurator.getTransactionRepository().delete(transaction);
        } catch (Throwable rollbackException) {
            logger.error("compensable transaction rollback failed.", rollbackException);
            throw new CancellingException(rollbackException);
        }
    }
}
