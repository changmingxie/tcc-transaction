package org.mengyun.tcctransaction.spring;

import org.aspectj.lang.annotation.Aspect;
import org.mengyun.tcctransaction.TccClient;
import org.mengyun.tcctransaction.interceptor.CompensableTransactionAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

/**
 * Created by changmingxie on 10/30/15.
 */
@Aspect
public class ConfigurableTransactionAspect extends CompensableTransactionAspect implements Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Autowired
    public void setTransactionManager(TransactionManagerFactory transactionManagerFactory) {
        super.setTransactionManager(transactionManagerFactory.getTransactionManager());
    }

    @Autowired
    public void setExtraDelayCancelExceptions(TransactionManagerFactory transactionManagerFactory) {
        if (TccClient.class.isAssignableFrom(transactionManagerFactory.getClass())) {
            TccClient tccClient = (TccClient) transactionManagerFactory;
            if (tccClient.getClientConfig().isEnableDelayCancel()) {
                super.addExtraDelayCancelExceptions(tccClient.getClientConfig().getDelayCancelExceptions());
            }
        }
    }
}
