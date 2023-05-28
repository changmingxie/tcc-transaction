package org.mengyun.tcctransaction.grpc.interceptor;

import io.grpc.*;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.TransactionContextHolder;
import org.mengyun.tcctransaction.grpc.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.serializer.TransactionContextSerializer;

/**
 * @author Nervose.Wu
 * @date 2022/6/24 16:37
 */
public class TransactionContextClientInterceptor implements ClientInterceptor {

    private TransactionContextSerializer transactionContextSerializer = new TransactionContextSerializer();

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        TransactionContext transactionContext = TransactionContextHolder.getCurrentTransactionContext();
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                if (transactionContext != null) {
                    headers.put(TransactionContextConstants.TRANSACTION_CONTEXT_HEADER_KEY, transactionContextSerializer.serialize(transactionContext));
                }
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {

                    @Override
                    public void onHeaders(Metadata headers) {
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
