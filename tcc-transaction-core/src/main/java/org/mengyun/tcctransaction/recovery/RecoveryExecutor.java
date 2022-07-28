package org.mengyun.tcctransaction.recovery;

import org.mengyun.tcctransaction.storage.TransactionStore;

public interface RecoveryExecutor {

    void rollback(TransactionStore transactionStore);

    void commit(TransactionStore transactionStore);

    byte[] transactionVisualize(String domain, byte[] content);
}
