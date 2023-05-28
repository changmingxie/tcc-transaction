package org.mengyun.tcctransaction.dubbo.interceptor;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.interceptor.ClusterInterceptor;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.context.ThreadLocalTransactionContextEditor;
import org.mengyun.tcctransaction.dubbo.DubboInvokeProceedingJoinPoint;
import org.mengyun.tcctransaction.dubbo.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorAspect;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import java.lang.reflect.Method;

@Activate(group = { CommonConstants.CONSUMER }, order = 1)
public class CompensableTransactionInterceptor implements ClusterInterceptor {

    @Override
    public void before(AbstractClusterInvoker<?> clusterInvoker, Invocation invocation) {
    }

    @Override
    public void after(AbstractClusterInvoker<?> clusterInvoker, Invocation invocation) {
    }

    @Override
    public Result intercept(AbstractClusterInvoker<?> invoker, Invocation invocation) throws RpcException {
        Method method = null;
        RpcContext.getContext().set(TransactionContextConstants.CLUSTER_INTERCEPTOR_TAKE_EFFECT_MARK, true);
        try {
            method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            EnableTcc enableTcc = method.getAnnotation(EnableTcc.class);
            if (enableTcc != null) {
                DubboInvokeProceedingJoinPoint pjp = new DubboInvokeProceedingJoinPoint(invoker, invocation, null, ThreadLocalTransactionContextEditor.class);
                return (Result) FactoryBuilder.factoryOf(ResourceCoordinatorAspect.class).getInstance().interceptTransactionContextMethod(pjp);
            } else {
                return invoker.invoke(invocation);
            }
        } catch (Throwable e) {
            throw new SystemException(e);
        }
    }
}
