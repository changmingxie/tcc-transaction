package org.mengyun.tcctransaction.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mengyun.tcctransaction.MethodType;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionConfigurator;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.spring.utils.CompensableMethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

/**
 * Created by changmingxie on 10/30/15.
 */
@Aspect
public class TccCompensableAspect implements Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE;

    @Autowired
    private TransactionConfigurator transactionConfigurator;

    @Pointcut("@annotation(org.mengyun.tcctransaction.spring.Compensable)")
    public void compensableService() {

    }

    @Around("compensableService()")
    public void interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

        TransactionContext transactionContext = CompensableMethodUtils.getTransactionContextFromArgs(pjp.getArgs());
        Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();

        MethodType methodType = CompensableMethodUtils.calculateMethodType(transactionContext, true);

        switch (methodType) {
            case ROOT:
                rootMethodProceed(pjp);
                break;
            case PROVIDER:
                providerMethodProceed(pjp, transactionContext);
                break;
            default:
                pjp.proceed();
        }
    }

    private void rootMethodProceed(ProceedingJoinPoint pjp) throws Throwable {

        transactionConfigurator.getTransactionManager().begin();

        try {
            pjp.proceed();
            transactionConfigurator.getTransactionManager().commit();

        } catch (Throwable e) {
            transactionConfigurator.getTransactionManager().rollback();
            throw e;
        }
    }

    private void providerMethodProceed(ProceedingJoinPoint pjp, TransactionContext transactionContext) throws Throwable {

        switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
            case TRYING:
                transactionConfigurator.getTransactionManager().propagationNewBegin(transactionContext);
                pjp.proceed();
                break;
            case CONFIRMING:
                transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                transactionConfigurator.getTransactionManager().commit();
                break;
            case CANCELLING:
                transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                transactionConfigurator.getTransactionManager().rollback();
                break;
        }

    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
