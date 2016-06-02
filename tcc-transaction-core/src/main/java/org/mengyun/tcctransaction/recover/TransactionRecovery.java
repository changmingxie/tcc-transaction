package org.mengyun.tcctransaction.recover;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.support.TransactionConfigurator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by changmingxie on 11/10/15.
 */
public class TransactionRecovery {

    static final Logger logger = Logger.getLogger(TransactionRecovery.class.getSimpleName());

    private TransactionConfigurator transactionConfigurator;

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    public void startRecover() {

        List<Transaction> transactions = loadErrorTransactions();

        recoverErrorTransactions(transactions);
    }

    private List<Transaction> loadErrorTransactions() {

        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();

        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

        List<Transaction> transactions = transactionRepository.findAllUnmodifiedSince(new Date(currentTimeInMillis - transactionConfigurator.getRecoverConfig().getRecoverDuration() * 1000));

        List<Transaction> recoverTransactions = new ArrayList<Transaction>();

        for (Transaction transaction : transactions) {

            int result = transactionRepository.update(transaction);

            if (result > 0) {
                recoverTransactions.add(transaction);
            }
        }

        return transactions;
    }

    private void recoverErrorTransactions(List<Transaction> transactions) {


        for (Transaction transaction : transactions) {

            if (transaction.getRetriedCount() > transactionConfigurator.getRecoverConfig().getMaxRetryCount()) {

                logger.error(String.format("recover failed with max retry count,will not try again. txid:%s, status:%s,retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()));
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
            } catch (Throwable e) {
                logger.warn(String.format("recover failed, txid:%s, status:%s,retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()), e);
            }
        }
    }

}
