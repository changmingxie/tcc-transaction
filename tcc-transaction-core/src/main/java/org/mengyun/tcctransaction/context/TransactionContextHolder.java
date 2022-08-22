package org.mengyun.tcctransaction.context;

import org.mengyun.tcctransaction.api.TransactionContext;

public class TransactionContextHolder {

    private static ThreadLocal<TransactionContext> transactionContextThreadLocal = new ThreadLocal<>();

    private TransactionContextHolder() {
    }

    public static TransactionContext getCurrentTransactionContext() {
        return transactionContextThreadLocal.get();
    }

    public static void setCurrentTransactionContext(TransactionContext transactionContext) {
        transactionContextThreadLocal.set(transactionContext);
    }

    public static void clear() {
        transactionContextThreadLocal.remove();
    }
}