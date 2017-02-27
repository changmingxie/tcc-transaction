package org.mengyun.tcctransaction.context;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionContextEditor;

import java.lang.reflect.Method;

/**
 * Created by changming.xie on 1/18/17.
 */
public class MethodTransactionContextEditor implements TransactionContextEditor {

    @Override
    public TransactionContext get() {
        return null;
    }

    @Override
    public void set(TransactionContext transactionContext, Object target, Method method, Object[] args) {

    }
}
