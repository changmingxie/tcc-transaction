package org.mengyun.tcctransaction.interceptor;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.mengyun.tcctransaction.NoExistedTransactionException;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.MethodType;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.mengyun.tcctransaction.utils.CompensableMethodUtils;

import java.util.ConcurrentModificationException;

/**
 * Created by changmingxie on 10/30/15.
 */
public class CompensableTransactionInterceptor {

    static final Logger logger = Logger.getLogger(CompensableTransactionInterceptor.class.getSimpleName());


    private TransactionConfigurator transactionConfigurator;


    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

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
        } catch (ConcurrentModificationException e) {
            throw e; //do not rollback, waiting for recovery job
        } catch (Throwable tryingException) {
            logger.warn("compensable transaction trying failed.", tryingException);
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


}
