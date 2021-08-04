package org.mengyun.tcctransaction.dubbo.filter;

import org.apache.dubbo.rpc.Constants;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.model.ConsumerMethodModel;
import org.apache.dubbo.rpc.model.ConsumerModel;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContextEditor;
import org.mengyun.tcctransaction.interceptor.TransactionMethodJoinPoint;

import java.lang.reflect.Method;

public class DubboInvokeProceedingJoinPoint implements TransactionMethodJoinPoint {

    private Invoker invoker;
    private Invocation invocation;
    Compensable compensable;
    Class<? extends TransactionContextEditor> transactionContextEditorClass;

    public DubboInvokeProceedingJoinPoint(Invoker invoker, Invocation invocation, Compensable compensable, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.invoker = invoker;
        this.invocation = invocation;
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
//        try {
//            return getTargetClass().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
//        } catch (NoSuchMethodException e) {
//            throw new SystemException(e);
//        }
        return ((ConsumerMethodModel) invocation.getAttributes().get(Constants.METHOD_MODEL)).getMethod();
    }

    @Override
    public Object getTarget() {
        return ((ConsumerModel) invocation.getAttributes().get(Constants.CONSUMER_MODEL)).getProxyObject();
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
