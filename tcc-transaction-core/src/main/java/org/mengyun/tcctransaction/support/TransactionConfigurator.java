package org.mengyun.tcctransaction.support;

import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.TransactionRepository;

/**
 * Created by changmingxie on 11/10/15.
 */
public interface TransactionConfigurator {

    public TransactionManager getTransactionManager();

    public TransactionRepository getTransactionRepository();

}
