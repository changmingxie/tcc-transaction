package org.mengyun.tcctransaction;

/**
 * Created by changmingxie on 11/10/15.
 */
public interface TransactionConfigurator {

    public TransactionManager getTransactionManager();

    public TransactionRepository getTransactionRepository();

}
