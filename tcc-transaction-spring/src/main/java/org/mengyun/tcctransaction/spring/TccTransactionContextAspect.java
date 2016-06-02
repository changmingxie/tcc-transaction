package org.mengyun.tcctransaction.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

/**
 * Created by changmingxie on 11/8/15.
 */
@Aspect
public class TccTransactionContextAspect implements Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE + 1;

    private ResourceCoordinatorInterceptor resourceCoordinatorInterceptor;

    @Pointcut("execution(public * *(org.mengyun.tcctransaction.api.TransactionContext,..))||@annotation(org.mengyun.tcctransaction.Compensable)")
    public void transactionContextCall() {

    }

    @Around("transactionContextCall()")
    public void interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {

        resourceCoordinatorInterceptor.interceptTransactionContextMethod(pjp);
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setResourceCoordinatorInterceptor(ResourceCoordinatorInterceptor resourceCoordinatorInterceptor) {
        this.resourceCoordinatorInterceptor = resourceCoordinatorInterceptor;
    }
}
