package org.mengyun.tcctransaction.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mengyun.tcctransaction.interceptor.CompensableTransactionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

/**
 * Created by changmingxie on 10/30/15.
 */
@Aspect
public class TccCompensableAspect implements Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE;

    @Autowired
    private CompensableTransactionInterceptor compensableTransactionInterceptor;

    @Pointcut("@annotation(org.mengyun.tcctransaction.Compensable)")
    public void compensableService() {

    }

    @Around("compensableService()")
    public void interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

        compensableTransactionInterceptor.interceptCompensableMethod(pjp);
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
