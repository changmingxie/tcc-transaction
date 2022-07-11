package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.recovery.TransactionStoreRecovery;

public interface TccService {

    void start() throws Exception;

    void shutdown() throws Exception;

    TransactionStoreRecovery getTransactionStoreRecovery();
}
