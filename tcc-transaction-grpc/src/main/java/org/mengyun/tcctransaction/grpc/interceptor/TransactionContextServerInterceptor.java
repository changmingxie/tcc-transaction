package org.mengyun.tcctransaction.grpc.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.grpc.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.serializer.TransactionContextSerializer;

/**
 * @author Nervose.Wu
 * @date 2022/6/24 16:36
 */
public class TransactionContextServerInterceptor implements ServerInterceptor {

    private TransactionContextSerializer serializer = new TransactionContextSerializer();

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        TransactionContext transactionContext = getTransactionContext(metadata);
        return new ServerListenerProxy<>(transactionContext, serverCallHandler.startCall(serverCall, metadata));
    }

    private TransactionContext getTransactionContext(Metadata metadata) {
        byte[] transactionContextBytes = metadata.get(TransactionContextConstants.TRANSACTION_CONTEXT_HEADER_KEY);
        return serializer.deserialize(transactionContextBytes);
    }
}
