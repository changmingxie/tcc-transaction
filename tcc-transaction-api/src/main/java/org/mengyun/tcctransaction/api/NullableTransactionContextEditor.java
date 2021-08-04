package org.mengyun.tcctransaction.api;

import java.lang.reflect.Method;

public class NullableTransactionContextEditor  implements TransactionContextEditor {

    @Override
    public TransactionContext get(Object target, Method method, Object[] args) {
        return null;
    }

    @Override
    public void set(TransactionContext transactionContext, Object target, Method method, Object[] args) {

    }
}