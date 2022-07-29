package org.mengyun.tcctransaction.transaction;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.TransactionContextEditor;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by changmingxie on 10/30/15.
 */
public final class Terminator {

    public Terminator() {

    }

    public static Object invoke(TransactionContext transactionContext, Invocation invocation, Class<? extends TransactionContextEditor> transactionContextEditorClass) {

        if (StringUtils.isNotEmpty(invocation.getMethodName())) {

            Object target = FactoryBuilder.factoryOf(invocation.getTargetClass()).getInstance();

            Method method = null;

            try {
                method = target.getClass().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new SystemException(e);
            }

            FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(transactionContext, target, method, invocation.getArgs());
            try {
                return method.invoke(target, invocation.getArgs());
            } catch (IllegalAccessException e) {
                throw new SystemException(e);
            } catch (InvocationTargetException e) {
                throw new SystemException(e);
            } finally {
                FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().clear(transactionContext, target, method, invocation.getArgs());
            }
        }
        return null;
    }
}
