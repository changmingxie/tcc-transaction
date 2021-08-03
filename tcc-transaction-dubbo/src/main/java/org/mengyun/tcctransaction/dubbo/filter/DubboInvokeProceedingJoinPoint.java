package org.mengyun.tcctransaction.dubbo.filter;

import org.apache.dubbo.rpc.Constants;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.model.ConsumerMethodModel;
import org.apache.dubbo.rpc.model.ConsumerModel;
import org.mengyun.tcctransaction.interceptor.TransactionMethodJoinPoint;

import java.lang.reflect.Method;

public class DubboInvokeProceedingJoinPoint implements TransactionMethodJoinPoint {

    private Invocation invocation;

    public DubboInvokeProceedingJoinPoint(Invocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public Class getTargetClass() {
        return invocation.getInvoker().getInterface();
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
        return invocation.getInvoker().invoke(invocation);
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        return proceed();
    }
}
