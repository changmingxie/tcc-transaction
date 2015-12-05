package org.mengyun.tcctransaction.spring.recover;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by changmingxie on 11/10/15.
 */
@Component
public class TransactionRecovery {

    static final Logger logger = Logger.getLogger(TransactionRecovery.class.getSimpleName());

    @Autowired
    private TransactionConfigurator transactionConfigurator;

    private boolean initialized = false;

    public void startRecover() {

        this.fireInitializationIfNecessary();

        Collection<Transaction> transactions = transactionConfigurator.getTransactionRepository().findAllErrorTransactions();

        List<Transaction> rollbackTransactions = new ArrayList<Transaction>(transactions);

        for (Transaction transaction : rollbackTransactions) {

            try {
                List<Participant> participants = transaction.getParticipants();

                for (Participant participant : participants) {
                    participant.rollback();
                }

                transactionConfigurator.getTransactionRepository().delete(transaction);
                transactionConfigurator.getTransactionRepository().removeErrorTransaction(transaction);
            } catch (Throwable e) {
                logger.warn("recover failed", e);
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
}
