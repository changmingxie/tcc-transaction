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

    public void startRecover() {

        List<Transaction> transactions = loadErrorTransactions();

        recoverErrorTransactions(transactions);
    }

    private List<Transaction> loadErrorTransactions() {


        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        RecoverConfig recoverConfig = transactionConfigurator.getRecoverConfig();
        
        List<Transaction> transactions = transactionRepository.findAllUnmodifiedSince(new Date(currentTimeInMillis - recoverConfig.getRecoverDuration() * 1000));

        List<Transaction> recoverTransactions = new ArrayList<Transaction>();

        for (Transaction transaction : transactions) {

            int result = transactionRepository.update(transaction);

            if (result > 0) {
                recoverTransactions.add(transaction);
            }
        }

        return recoverTransactions;
    }

    private void recoverErrorTransactions(List<Transaction> transactions) {


        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        RecoverConfig recoverConfig = transactionConfigurator.getRecoverConfig();

        for (Transaction transaction : transactions) {

            if (transaction.getRetriedCount() > recoverConfig.getMaxRetryCount()) {

                logger.error(String.format("recover failed with max retry count,will not try again. txid:%s, status:%s,retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()));
                continue;
            }

            try {
                transaction.addRetriedCount();

                if (transaction.getStatus().equals(TransactionStatus.CONFIRMING)) {
                    transaction.changeStatus(TransactionStatus.CONFIRMING);
                    transactionRepository.update(transaction);
                    transaction.commit();

                } else {
                    transaction.changeStatus(TransactionStatus.CANCELLING);
                    transactionRepository.update(transaction);
                    transaction.rollback();
                }

                transactionRepository.delete(transaction);
            } catch (Throwable e) {
                logger.warn(String.format("recover failed, txid:%s, status:%s,retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()), e);
            }
        }
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
