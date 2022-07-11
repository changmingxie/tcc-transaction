package org.mengyun.tcctransaction.dubbo;

import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.context.TransactionContextEditor;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.interceptor.TransactionMethodJoinPoint;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.lang.reflect.Method;

public class DubboInvokeProceedingJoinPoint implements TransactionMethodJoinPoint {

    Compensable compensable;
    Class<? extends TransactionContextEditor> transactionContextEditorClass;
    private Invoker invoker;
    private Invocation invocation;
    private Method method = null;
    private Object target;

    public DubboInvokeProceedingJoinPoint(Invoker invoker, Invocation invocation, Compensable compensable, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.invoker = invoker;
        this.invocation = invocation;
        this.compensable = compensable;
        this.transactionContextEditorClass = transactionContextEditorClass;

        try {
            method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new SystemException(e);
        }

        target = FactoryBuilder.factoryOf(getDeclaredClass()).getInstance();
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
    public Class<?> getDeclaredClass() {
        return invoker.getInterface();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object[] getArgs() {
        return invocation.getArguments();
    }

    @Override
    public Object proceed() throws Throwable {
        return invoker.invoke(invocation);
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        return proceed();
    }
}
