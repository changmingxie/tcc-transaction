package org.mengyun.tcctransaction.spring;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mengyun.tcctransaction.MethodType;
import org.mengyun.tcctransaction.NoExistedTransactionException;
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
    static final Logger logger = Logger.getLogger(TccCompensableAspect.class.getSimpleName());

    @Autowired
    private TransactionConfigurator transactionConfigurator;

    @Pointcut("@annotation(org.mengyun.tcctransaction.spring.Compensable)")
    public void compensableService() {

    }

    @Around("compensableService()")
    public void interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

        TransactionContext transactionContext = CompensableMethodUtils.getTransactionContextFromArgs(pjp.getArgs());

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
        } catch (Throwable tryingException) {
            logger.error("compensable transaction trying failed.", tryingException);
            try {
                transactionConfigurator.getTransactionManager().rollback();
            } catch (Throwable rollbackException) {
                logger.error("compensable transaction rollback failed.", rollbackException);
                throw rollbackException;
            }

            throw tryingException;
        }

        transactionConfigurator.getTransactionManager().commit();
    }

    private void providerMethodProceed(ProceedingJoinPoint pjp, TransactionContext transactionContext) throws Throwable {

        switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
            case TRYING:
                transactionConfigurator.getTransactionManager().propagationNewBegin(transactionContext);
                pjp.proceed();
                break;
            case CONFIRMING:
                try {
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().commit();
                } catch (NoExistedTransactionException excepton) {
                    //the transaction has been commit,ignore it.
                }
                break;
            case CANCELLING:

                try {
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().rollback();
                } catch (NoExistedTransactionException exception) {
                    //the transaction has been rollback,ignore it.
                }
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
