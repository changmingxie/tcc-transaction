package org.mengyun.tcctransaction.storage;

/**
 * Created by changming.xie on 7/21/16.
 */
public class TransactionOptimisticLockException extends TransactionIOException {

    public TransactionOptimisticLockException() {
    }

    public TransactionOptimisticLockException(String message) {
        super(message);
    }

    public TransactionOptimisticLockException(Throwable e) {
        super(e);
    }
}
