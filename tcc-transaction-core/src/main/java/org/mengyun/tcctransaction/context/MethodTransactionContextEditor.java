package org.mengyun.tcctransaction.context;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionContextEditor;
import org.mengyun.tcctransaction.utils.CompensableMethodUtils;

import java.lang.reflect.Method;

/**
 * Created by changming.xie on 1/18/17.
 * this class is replaced by org.mengyun.tcctransaction.api.Compensable.DefaultTransactionContextEditor
 */
@Deprecated
public class MethodTransactionContextEditor implements TransactionContextEditor {

    @Override
    public TransactionContext get(Object target, Method method, Object[] args) {
        int position = CompensableMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());

        if (position >= 0) {
            return (TransactionContext) args[position];
        }
        
        return null;
    }

    @Override
    public void set(TransactionContext transactionContext, Object target, Method method, Object[] args) {

        int position = CompensableMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());
        if (position >= 0) {
            args[position] = transactionContext;
        }
    }
}
