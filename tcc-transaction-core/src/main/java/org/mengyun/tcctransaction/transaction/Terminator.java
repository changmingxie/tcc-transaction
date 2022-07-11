package org.mengyun.tcctransaction.transaction;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.TransactionContextEditor;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by changmingxie on 10/30/15.
 */
public final class Terminator {

    public Terminator() {

    }

    public static Object invoke(TransactionContext transactionContext, InvocationContext invocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {


        if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {

            Object target = FactoryBuilder.factoryOf(invocationContext.getTargetClass()).getInstance();

            Method method = null;

            try {
                method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new SystemException(e);
            }

            FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(transactionContext, target, method, invocationContext.getArgs());
            try {
                return method.invoke(target, invocationContext.getArgs());
            } catch (IllegalAccessException e) {
                throw new SystemException(e);
            } catch (InvocationTargetException e) {
                throw new SystemException(e);
            } finally {
                FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().clear(transactionContext, target, method, invocationContext.getArgs());
            }
        }
        return null;
    }
}
