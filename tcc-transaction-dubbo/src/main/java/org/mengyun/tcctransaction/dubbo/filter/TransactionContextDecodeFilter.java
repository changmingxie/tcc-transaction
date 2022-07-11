package org.mengyun.tcctransaction.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.mengyun.tcctransaction.context.TransactionContextHolder;
import org.mengyun.tcctransaction.dubbo.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.serializer.TransactionContextSerializer;

@Activate(group = {CommonConstants.PROVIDER}, order = 0)
public class TransactionContextDecodeFilter implements Filter {

    private TransactionContextSerializer transactionContextSerializer = new TransactionContextSerializer();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        byte[] context = (byte[]) RpcContext.getContext().getObjectAttachment(TransactionContextConstants.TRANSACTION_CONTEXT);

        if (context != null) {
            TransactionContextHolder.setCurrentTransactionContext(transactionContextSerializer.deserialize(context));
        }
        return invoker.invoke(invocation);
    }
}
