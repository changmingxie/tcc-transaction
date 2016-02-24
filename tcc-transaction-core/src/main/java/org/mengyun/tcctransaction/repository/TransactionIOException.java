package org.mengyun.tcctransaction.repository;

/**
 * Created by hongyuan.wang on 2016/1/26.
 */
public class TransactionIOException extends RuntimeException {

    public TransactionIOException(String message) {
        super(message);
    }

    public TransactionIOException(Throwable e) {
        super(e);
    }
}
