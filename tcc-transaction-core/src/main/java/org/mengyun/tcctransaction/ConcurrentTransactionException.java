package org.mengyun.tcctransaction;

/**
 * Created by changming.xie on 10/04/19.
 */
public class ConcurrentTransactionException extends RuntimeException {
    private static final long serialVersionUID = 4099060614687283527L;

    public ConcurrentTransactionException() {

    }

    public ConcurrentTransactionException(String message) {
        super(message);
    }

    public ConcurrentTransactionException(String message, Throwable e) {
        super(message, e);
    }
}
