package org.mengyun.tcctransaction.http.springmvc.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.context.TransactionContextHolder;
import org.mengyun.tcctransaction.http.constants.TransactionContextConstants;
import org.mengyun.tcctransaction.serializer.TransactionContextSerializer;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

public class RequesterInterceptor implements HandlerInterceptor {

    private TransactionContextSerializer transactionContextSerializer = new TransactionContextSerializer();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String transactionContext = request.getHeader(TransactionContextConstants.TRANSACTION_CONTEXT);
        if (StringUtils.isNotEmpty(transactionContext)) {
            TransactionContextHolder.setCurrentTransactionContext(transactionContextSerializer.deserialize(Base64.getDecoder().decode(transactionContext)));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        String transactionContext = request.getHeader(TransactionContextConstants.TRANSACTION_CONTEXT);
        if (StringUtils.isNotEmpty(transactionContext)) {
            TransactionContextHolder.clear();
        }
    }
}
