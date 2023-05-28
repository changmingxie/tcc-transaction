package org.mengyun.tcctransaction.context;

import org.mengyun.tcctransaction.api.TransactionContext;
import java.lang.reflect.Method;

public class ThreadLocalTransactionContextEditor implements TransactionContextEditor {

    @Override
    public TransactionContext get(Object target, Method method, Object[] args) {
        return TransactionContextHolder.getCurrentTransactionContext();
    }

    @Override
    public void set(TransactionContext transactionContext, Object target, Method method, Object[] args) {
        TransactionContextHolder.setCurrentTransactionContext(transactionContext);
    }

    public void clear(TransactionContext transactionContext, Object target, Method method, Object[] args) {
        TransactionContextHolder.clear();
    }
}
