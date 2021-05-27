package org.mengyun.tcctransaction.interceptor;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.mengyun.tcctransaction.IllegalTransactionStatusException;
import org.mengyun.tcctransaction.NoExistedTransactionException;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.api.ParticipantStatus;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by changmingxie on 10/30/15.
 */
public class CompensableTransactionInterceptor {

    static final Logger logger = LoggerFactory.getLogger(CompensableTransactionInterceptor.class.getSimpleName());

    private TransactionManager transactionManager;

//    private Set<Class<? extends Exception>> delayCancelExceptions = new HashSet<Class<? extends Exception>>();

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
//
//    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayCancelExceptions) {
//        this.delayCancelExceptions.addAll(delayCancelExceptions);
//    }

    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

        Transaction transaction = transactionManager.getCurrentTransaction();
        CompensableMethodContext compensableMethodContext = new CompensableMethodContext(pjp, transaction);

//        if (!TransactionUtils.isLegalTransactionContext(isTransactionActive, compensableMethodContext)) {
//            throw new SystemException("no active compensable transaction while propagation is mandatory for method " + compensableMethodContext.getMethod().getName());
//        }

        // if method is @Compensable and no transaction context and no transaction, then root
        // else if method is @Compensable and has transaction context and no transaction ,then provider
        switch (compensableMethodContext.getParticipantRole()) {
            case ROOT:
                return rootMethodProceed(compensableMethodContext);
            case PROVIDER:
                return providerMethodProceed(compensableMethodContext);
            default:
                return pjp.proceed();
        }
    }


    private Object rootMethodProceed(CompensableMethodContext compensableMethodContext) throws Throwable {

        Object returnValue = null;

        Transaction transaction = null;

        boolean asyncConfirm = compensableMethodContext.getAnnotation().asyncConfirm();

        boolean asyncCancel = compensableMethodContext.getAnnotation().asyncCancel();

//        Set<Class<? extends Exception>> allDelayCancelExceptions = new HashSet<Class<? extends Exception>>();
//        allDelayCancelExceptions.addAll(this.delayCancelExceptions);
//        allDelayCancelExceptions.addAll(Arrays.asList(compensableMethodContext.getAnnotation().delayCancelExceptions()));

        try {

            transaction = transactionManager.begin(compensableMethodContext.getUniqueIdentity());

            try {
                returnValue = compensableMethodContext.proceed();
            } catch (Throwable tryingException) {

//                if (!isDelayCancelException(tryingException, allDelayCancelExceptions)) {
//
//                    logger.warn(String.format("compensable transaction trying failed. transaction content:%s", JSON.toJSONString(transaction)), tryingException);
//
//                    transactionManager.rollback(asyncCancel);
//                }

                transactionManager.rollback(asyncCancel);

                throw tryingException;
            }

            transactionManager.commit(asyncConfirm);

        } finally {
            transactionManager.cleanAfterCompletion(transaction);
        }

        return returnValue;
    }

    private Object providerMethodProceed(CompensableMethodContext compensableMethodContext) throws Throwable {

        Transaction transaction = null;


        boolean asyncConfirm = compensableMethodContext.getAnnotation().asyncConfirm();

        boolean asyncCancel = compensableMethodContext.getAnnotation().asyncCancel();

        try {

            switch (TransactionStatus.valueOf(compensableMethodContext.getTransactionContext().getStatus())) {
                case TRYING:
                    transaction = transactionManager.propagationNewBegin(compensableMethodContext.getTransactionContext());
                    return compensableMethodContext.proceed();
                case CONFIRMING:
                    try {
                        transaction = transactionManager.propagationExistBegin(compensableMethodContext.getTransactionContext());
                        transactionManager.commit(asyncConfirm);
                    } catch (NoExistedTransactionException excepton) {
                        //the transaction has been commit,ignore it.
                        logger.info("no existed transaction found at CONFIRMING stage, will ignore and confirm automatically. transaction:" + JSON.toJSONString(transaction));
                    }
                    break;
                case CANCELLING:

                    try {
                        transaction = transactionManager.propagationExistBegin(compensableMethodContext.getTransactionContext());


                        //The participant' status of this branch transaction, passed from consumer side.
                        int participantStatus = compensableMethodContext.getTransactionContext().getParticipantStatus();

                        if (participantStatus == ParticipantStatus.TRY_FAILED.getId()) {

                            if (transaction.isTryFailed()) {
                                // In this case, the  TRY_FAILED status of the participant passed from consumer side is caused by any of the transaction's participants,
                                // can safely rollback the transaction.
                                transactionManager.rollback(asyncCancel);
                            } else {
                                // In this case, the participant' status of this branch transaction passed from consumer side is TRY_FAILED,
                                // while the transaction has no any try failed participant, which means the participant's status seem from consumer side
                                // is not caused by any of the transaction participant, maybe caused by timeout exception or another.
                                // The transaction's participants maybe still running(try stage), cannot directly rollback, need waiting for the recovery job to rollback it.
                                throw new IllegalTransactionStatusException("Branch transaction maybe is trying, cannot rollback directly, waiting for recovery job to rollback.");
                            }
                        } else {
                            //The status of participant of this branch transaction is TRYING_SUCCESS, can safely rollback the transaction, too
                            transactionManager.rollback(asyncCancel);
                        }

                    } catch (NoExistedTransactionException exception) {
                        //the transaction has been rollback,ignore it.
                        logger.info("no existed transaction found at CANCELLING stage, will ignore and cancel automatically. transaction:" + JSON.toJSONString(transaction));
                    }
                    break;
            }

        } finally {
            transactionManager.cleanAfterCompletion(transaction);
        }

        Method method = compensableMethodContext.getMethod();

        return ReflectionUtils.getNullValue(method.getReturnType());
    }

    private boolean isDelayCancelException(Throwable throwable, Set<Class<? extends Exception>> delayCancelExceptions) {

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
