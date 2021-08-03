package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class AspectJTransactionMethodJoinPoint implements TransactionMethodJoinPoint {

    ProceedingJoinPoint pjp;

    public AspectJTransactionMethodJoinPoint(ProceedingJoinPoint pjp) {
        this.pjp = pjp;
    }

    @Override
    public Class getTargetClass() {
        return pjp.getTarget().getClass();
    }

    @Override
    public Method getMethod() {
        return ((MethodSignature) pjp.getSignature()).getMethod();
    }

    @Override
    public Object getTarget() {
        return pjp.getTarget();
    }

    @Override
    public Object[] getArgs() {
        return pjp.getArgs();
    }

    @Override
    public Object proceed() throws Throwable {
        return pjp.proceed();
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        return pjp.proceed(args);
    }
}
