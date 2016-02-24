package org.mengyun.tcctransaction.recover;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.support.TransactionConfigurator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by changmingxie on 11/10/15.
 */
public class TransactionRecovery {

    private int maxRetryCount = 3;

    static final Logger logger = Logger.getLogger(TransactionRecovery.class.getSimpleName());

    private TransactionConfigurator transactionConfigurator;

    private volatile boolean initialized = false;

    public void startRecover() {

        this.fireInitializationIfNecessary();

        Collection<Transaction> transactions = transactionConfigurator.getTransactionRepository().findAllErrorTransactions();

        List<Transaction> rollbackTransactions = new ArrayList<Transaction>(transactions);

        for (Transaction transaction : rollbackTransactions) {

            if (transaction.getRetriedCount() > maxRetryCount) {

                transactionConfigurator.getTransactionRepository().removeErrorTransaction(transaction);
                continue;
            }

            try {
                transaction.addRetriedCount();

                if (transaction.getStatus().equals(TransactionStatus.CONFIRMING)) {
                    transaction.changeStatus(TransactionStatus.CONFIRMING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    transaction.commit();

                } else {
                    transaction.changeStatus(TransactionStatus.CANCELLING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    transaction.rollback();
                }

                transactionConfigurator.getTransactionRepository().delete(transaction);
                transactionConfigurator.getTransactionRepository().removeErrorTransaction(transaction);
            } catch (Throwable e) {
                logger.error(String.format("recover failed, txid:%s, status:%s,retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()), e);
            }
        }
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    private void fireInitializationIfNecessary() {
        if (this.initialized == false) {
            this.processStartupRecover();
            this.initialized = true;
        }
    }

    private synchronized void processStartupRecover() {

        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();

        List<Transaction> transactions = transactionRepository.findAll();

        for (int i = 0; i < transactions.size(); i++) {

            Transaction transaction = transactions.get(i);

            if (transaction.getTransactionType().equals(TransactionType.ROOT)) {
                transactionConfigurator.getTransactionRepository().addErrorTransaction(transaction);
            }
        }
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
}
