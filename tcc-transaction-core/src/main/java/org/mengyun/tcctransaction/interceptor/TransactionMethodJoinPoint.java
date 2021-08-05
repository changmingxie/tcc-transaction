package org.mengyun.tcctransaction.interceptor;

import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContextEditor;

import java.lang.reflect.Method;

public interface TransactionMethodJoinPoint {

    Compensable getCompensable();

    Class<? extends TransactionContextEditor> getTransactionContextEditorClass();

    Method getMethod();

    Object getTarget();

    Object[] getArgs();

    Object proceed() throws Throwable;

    Object proceed(Object[] args) throws Throwable;


}
