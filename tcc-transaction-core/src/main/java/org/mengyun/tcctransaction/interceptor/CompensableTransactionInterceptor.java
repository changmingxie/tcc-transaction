package org.mengyun.tcctransaction.interceptor;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.NoExistedTransactionException;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.MethodType;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.CompensableMethodUtils;
import org.mengyun.tcctransaction.utils.ReflectionUtils;
import org.mengyun.tcctransaction.utils.TransactionUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by changmingxie on 10/30/15.
 */
public class CompensableTransactionInterceptor {

    static final Logger logger = Logger.getLogger(CompensableTransactionInterceptor.class.getSimpleName());

    private TransactionManager transactionManager;

    private Set<Class<? extends Exception>> delayCancelExceptions;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayCancelExceptions) {
        this.delayCancelExceptions = delayCancelExceptions;
    }

    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

        Method method = CompensableMethodUtils.getCompensableMethod(pjp);

        Compensable compensable = method.getAnnotation(Compensable.class);
        Propagation propagation = compensable.propagation();
        TransactionContext transactionContext = FactoryBuilder.factoryOf(compensable.transactionContextEditor()).getInstance().get(pjp.getTarget(), method, pjp.getArgs());

        boolean isTransactionActive = transactionManager.isTransactionActive();

        if (!TransactionUtils.isLegalTransactionContext(isTransactionActive, propagation, transactionContext)) {
            throw new SystemException("no active compensable transaction while propagation is mandatory for method " + method.getName());
        }

        MethodType methodType = CompensableMethodUtils.calculateMethodType(propagation, isTransactionActive, transactionContext);

        switch (methodType) {
            case ROOT:
                return rootMethodProceed(pjp);
            case PROVIDER:
                return providerMethodProceed(pjp, transactionContext);
            default:
                return pjp.proceed();
        }
    }


    private Object rootMethodProceed(ProceedingJoinPoint pjp) throws Throwable {

        Object returnValue = null;

        Transaction transaction = null;

        try {

            transaction = transactionManager.begin();

            try {
                returnValue = pjp.proceed();
            } catch (Throwable tryingException) {

                if (isDelayCancelException(tryingException)) {

                } else {
                    logger.warn(String.format("compensable transaction trying failed. transaction content:%s", JSON.toJSONString(transaction)), tryingException);

                    transactionManager.rollback();
                }

                throw tryingException;
            }

            transactionManager.commit();

        } finally {
            transactionManager.cleanAfterCompletion(transaction);
        }

        return returnValue;
    }

    private Object providerMethodProceed(ProceedingJoinPoint pjp, TransactionContext transactionContext) throws Throwable {

        Transaction transaction = null;
        try {

            switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
                case TRYING:
                    transaction = transactionManager.propagationNewBegin(transactionContext);
                    return pjp.proceed();
                case CONFIRMING:
                    try {
                        transaction = transactionManager.propagationExistBegin(transactionContext);
                        transactionManager.commit();
                    } catch (NoExistedTransactionException excepton) {
                        //the transaction has been commit,ignore it.
                    }
                    break;
                case CANCELLING:

                    try {
                        transaction = transactionManager.propagationExistBegin(transactionContext);
                        transactionManager.rollback();
                    } catch (NoExistedTransactionException exception) {
                        //the transaction has been rollback,ignore it.
                    }
                    break;
            }

        } finally {
            transactionManager.cleanAfterCompletion(transaction);
        }

        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

        return ReflectionUtils.getNullValue(method.getReturnType());
    }

    private boolean isDelayCancelException(Throwable throwable) {

        if (delayCancelExceptions != null) {
            for (Class delayCancelException : delayCancelExceptions) {

                Throwable rootCause = ExceptionUtils.getRootCause(throwable);

                if (delayCancelException.isAssignableFrom(throwable.getClass())
                        || (rootCause != null && delayCancelException.isAssignableFrom(rootCause.getClass()))) {
                    return true;
                }
            }
        }

        return false;
    }

}
