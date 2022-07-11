package org.mengyun.tcctransaction.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.TransactionContextHolder;
import org.mengyun.tcctransaction.dubbo.Interceptor.CompensableTransactionInterceptor;
import org.mengyun.tcctransaction.dubbo.Interceptor.TransactionContextEncodeInterceptor;
import org.mengyun.tcctransaction.dubbo.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.serializer.TransactionContextSerializer;

import java.lang.reflect.Method;

/**
 * @author Nervose.Wu
 * @date 2022/6/29 19:18
 */

@Activate(group = {CommonConstants.CONSUMER}, order = 1)
public class TransactionContextEncodeFilter implements Filter {

    private TransactionContextSerializer transactionContextSerializer = new TransactionContextSerializer();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        /**
         * only used in direct connection mode in which case ClusterInterceptor doesn't work
         * @see CompensableTransactionInterceptor
         * @see TransactionContextEncodeInterceptor
         */
        if (RpcContext.getContext().get(TransactionContextConstants.CLUSTER_INTERCEPTOR_TAKE_EFFECT_MARK) != null) {
            return invoker.invoke(invocation);
        }


        Method method = null;

        try {
            method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());

            EnableTcc enableTcc = method.getAnnotation(EnableTcc.class);

            if (enableTcc != null) {
                TransactionContext transactionContext = TransactionContextHolder.getCurrentTransactionContext();
                if (transactionContext != null) {
                    RpcContext.getContext().setAttachment(TransactionContextConstants.TRANSACTION_CONTEXT, transactionContextSerializer.serialize(transactionContext));
                }
            }
            return invoker.invoke(invocation);
        } catch (Throwable e) {
            throw new SystemException(e);
        }
    }
}