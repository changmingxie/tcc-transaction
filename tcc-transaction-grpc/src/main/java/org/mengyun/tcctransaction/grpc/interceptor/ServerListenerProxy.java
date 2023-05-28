package org.mengyun.tcctransaction.grpc.interceptor;

import io.grpc.ServerCall;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.TransactionContextHolder;
import java.util.Objects;

/**
 * @author Nervose.Wu
 * @date 2022/6/24 17:43
 */
public class ServerListenerProxy<ReqT> extends ServerCall.Listener<ReqT> {

    private ServerCall.Listener<ReqT> target;

    private TransactionContext context;

    public ServerListenerProxy(TransactionContext context, ServerCall.Listener<ReqT> target) {
        super();
        Objects.requireNonNull(target);
        this.target = target;
        this.context = context;
    }

    @Override
    public void onMessage(ReqT message) {
        target.onMessage(message);
    }

    @Override
    public void onHalfClose() {
        if (context != null) {
            TransactionContextHolder.setCurrentTransactionContext(context);
        }
        target.onHalfClose();
    }

    @Override
    public void onCancel() {
        TransactionContextHolder.clear();
        target.onCancel();
    }

    @Override
    public void onComplete() {
        TransactionContextHolder.clear();
        target.onComplete();
    }

    @Override
    public void onReady() {
        target.onReady();
    }
}
