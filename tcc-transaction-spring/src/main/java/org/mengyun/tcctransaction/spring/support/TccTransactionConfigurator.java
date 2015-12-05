package org.mengyun.tcctransaction.spring.support;

import org.mengyun.tcctransaction.TransactionConfigurator;
import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by changmingxie on 11/11/15.
 */
@Component
public class TccTransactionConfigurator implements TransactionConfigurator {

    @Autowired
    private TransactionRepository transactionRepository;

    private TransactionManager transactionManager = new TransactionManager(this);

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

}
