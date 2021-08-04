package org.mengyun.tcctransaction.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.api.ParameterTransactionContextEditor;
import org.mengyun.tcctransaction.dubbo.context.DubboTransactionContextEditor;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorAspect;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.lang.reflect.Method;

@Activate(group = {Constants.CONSUMER})
public class CompensableTransactionFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

//        return invoker.invoke(invocation);
        return doInvoke(invoker, invocation);
    }

    private Result doInvoke(Invoker<?> invoker, Invocation invocation) {

        Method method = null;

        try {

            method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());

            boolean hasTransactionContextParameter = ParameterTransactionContextEditor.getTransactionContextParamPosition(invocation.getParameterTypes()) > 0;

            if (hasTransactionContextParameter) {
                // in this case, will handler by ResourceCoordinatorAspect
                return invoker.invoke(invocation);
            }

            EnableTcc enableTcc = method.getAnnotation(EnableTcc.class);

            if (enableTcc != null) {
                DubboInvokeProceedingJoinPoint pjp = new DubboInvokeProceedingJoinPoint(invoker, invocation, null, DubboTransactionContextEditor.class);
                return (Result) FactoryBuilder.factoryOf(ResourceCoordinatorAspect.class).getInstance().interceptTransactionContextMethod(pjp);
            } else {
                return invoker.invoke(invocation);
            }

        } catch (Throwable e) {
            throw new SystemException(e);
        }
    }
}
