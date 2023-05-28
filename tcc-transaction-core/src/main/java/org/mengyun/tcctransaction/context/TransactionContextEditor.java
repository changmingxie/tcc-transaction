package org.mengyun.tcctransaction.context;

import org.mengyun.tcctransaction.api.TransactionContext;
import java.lang.reflect.Method;

/**
 * Created by changming.xie on 1/18/17.
 */
public interface TransactionContextEditor {

    TransactionContext get(Object target, Method method, Object[] args);

    void set(TransactionContext transactionContext, Object target, Method method, Object[] args);

    default void clear(TransactionContext transactionContext, Object target, Method method, Object[] args) {
    }
}
