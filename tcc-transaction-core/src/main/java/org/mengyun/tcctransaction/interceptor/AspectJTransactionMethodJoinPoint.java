package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContextEditor;

import java.lang.reflect.Method;

public class AspectJTransactionMethodJoinPoint implements TransactionMethodJoinPoint {

    ProceedingJoinPoint pjp;
    Compensable compensable;
    Class<? extends TransactionContextEditor> transactionContextEditorClass;

    public AspectJTransactionMethodJoinPoint(ProceedingJoinPoint pjp, Compensable compensable, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.pjp = pjp;
        this.compensable = compensable;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    @Override
    public Compensable getCompensable() {
        return compensable;
    }

    @Override
    public Class<? extends TransactionContextEditor> getTransactionContextEditorClass() {
        return transactionContextEditorClass;
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
