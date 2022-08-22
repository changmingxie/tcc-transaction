package org.mengyun.tcctransaction.dubbo.interceptor;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.cluster.interceptor.ClusterInterceptor;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.TransactionContextHolder;
import org.mengyun.tcctransaction.dubbo.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.serializer.TransactionContextSerializer;

import java.lang.reflect.Method;

@Activate(group = {CommonConstants.CONSUMER}, order = 2)
public class TransactionContextEncodeInterceptor implements ClusterInterceptor {

    private TransactionContextSerializer transactionContextSerializer = new TransactionContextSerializer();


    @Override
    public void before(AbstractClusterInvoker<?> invoker, Invocation invocation) {
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
        } catch (Throwable e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void after(AbstractClusterInvoker<?> clusterInvoker, Invocation invocation) {

    }
}
