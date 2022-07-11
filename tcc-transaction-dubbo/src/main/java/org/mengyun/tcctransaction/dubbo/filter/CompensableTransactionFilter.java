package org.mengyun.tcctransaction.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.context.ThreadLocalTransactionContextEditor;
import org.mengyun.tcctransaction.dubbo.DubboInvokeProceedingJoinPoint;
import org.mengyun.tcctransaction.dubbo.Interceptor.CompensableTransactionInterceptor;
import org.mengyun.tcctransaction.dubbo.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorAspect;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.lang.reflect.Method;

/**
 *
 * @author Nervose.Wu
 * @date 2022/6/29 19:18
 */
@Activate(group = {CommonConstants.CONSUMER}, order = 0)
public class CompensableTransactionFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        /**
         * only used in direct connection mode in which case ClusterInterceptor doesn't work
         * @see CompensableTransactionInterceptor
         */
        if (RpcContext.getContext().get(TransactionContextConstants.CLUSTER_INTERCEPTOR_TAKE_EFFECT_MARK) != null) {
            return invoker.invoke(invocation);
        }

        Method method = null;

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
