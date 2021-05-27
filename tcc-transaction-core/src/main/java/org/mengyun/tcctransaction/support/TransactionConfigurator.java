package org.mengyun.tcctransaction.support;


import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.recovery.RecoverFrequency;
import org.mengyun.tcctransaction.repository.TransactionRepository;

import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * Created by changming.xie on 2/24/17.
 */
public interface TransactionConfigurator {

    TransactionManager getTransactionManager();

    TransactionRepository getTransactionRepository();

    RecoverFrequency getRecoverFrequency();

    Lock getRecoveryLock();
}
