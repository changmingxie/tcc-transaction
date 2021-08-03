package org.mengyun.tcctransaction.interceptor;

import java.lang.reflect.Method;

public interface TransactionMethodJoinPoint {

    Class getTargetClass();

    Method getMethod();

    Object getTarget();

    Object[] getArgs();

    Object proceed() throws Throwable;

    Object proceed(Object[] var1) throws Throwable;


}
