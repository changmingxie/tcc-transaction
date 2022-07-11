package org.mengyun.tcctransaction.spring;

import org.mengyun.tcctransaction.transaction.TransactionManager;

public interface TransactionManagerFactory {
    TransactionManager getTransactionManager();
}
