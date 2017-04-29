package org.mengyun.tcctransaction.spring.support;

import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.recover.RecoverConfig;
import org.mengyun.tcctransaction.repository.CachableTransactionRepository;
import org.mengyun.tcctransaction.spring.recover.DefaultRecoverConfig;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by changmingxie on 11/11/15.
 */
public class SpringTransactionConfigurator implements TransactionConfigurator {


    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired(required = false)
    private RecoverConfig recoverConfig = DefaultRecoverConfig.INSTANCE;


    private TransactionManager transactionManager;

    public void init() {
        transactionManager = new TransactionManager();
        transactionManager.setTransactionRepository(transactionRepository);

        if (transactionRepository instanceof CachableTransactionRepository) {
            ((CachableTransactionRepository) transactionRepository).setExpireDuration(recoverConfig.getRecoverDuration());
        }

    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    @Override
    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }
}
