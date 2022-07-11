package org.mengyun.tcctransaction.http.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.context.TransactionContextHolder;
import org.mengyun.tcctransaction.http.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.serializer.TransactionContextSerializer;

import java.lang.annotation.Annotation;
import java.util.Base64;

public class FeignInterceptor implements RequestInterceptor {

    private TransactionContextSerializer transactionContextSerializer = new TransactionContextSerializer();

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Annotation annotation = requestTemplate.methodMetadata().method().getAnnotation(EnableTcc.class);

        if (annotation != null && TransactionContextHolder.getCurrentTransactionContext() != null) {
            requestTemplate.header(TransactionContextConstants.TRANSACTION_CONTEXT,
                    Base64.getEncoder().encodeToString(transactionContextSerializer.serialize(TransactionContextHolder.getCurrentTransactionContext())));
        }

        return;
    }
}
