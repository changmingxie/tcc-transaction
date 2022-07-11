package org.mengyun.tcctransaction.interceptor;

import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.context.TransactionContextEditor;

import java.lang.reflect.Method;

public interface TransactionMethodJoinPoint {

    Compensable getCompensable();

    Class<? extends TransactionContextEditor> getTransactionContextEditorClass();

    Class<?> getDeclaredClass();

    Method getMethod();

    Object getTarget();

    Object[] getArgs();

    Object proceed() throws Throwable;

    Object proceed(Object[] args) throws Throwable;


}
