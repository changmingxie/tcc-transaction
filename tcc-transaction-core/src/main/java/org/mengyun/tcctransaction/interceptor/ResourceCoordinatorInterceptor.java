package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.InvocationContext;
import org.mengyun.tcctransaction.Participant;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.api.*;
import org.mengyun.tcctransaction.common.ParticipantRole;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.ReflectionUtils;

/**
 * Created by changmingxie on 11/8/15.
 */
public class ResourceCoordinatorInterceptor {

    private TransactionManager transactionManager;


    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {

        Transaction transaction = transactionManager.getCurrentTransaction();

        if (transaction != null && transaction.getStatus().equals(TransactionStatus.TRYING)) {

            Participant participant = enlistParticipant(pjp);

            if (participant != null) {

                Object result = null;
                try {
                    result = pjp.proceed(pjp.getArgs());
                    participant.setStatus(ParticipantStatus.TRY_SUCCESS);
                } catch (Throwable e) {
                    participant.setStatus(ParticipantStatus.TRY_FAILED);
                    transactionManager.update(participant);
                    throw e;
                }

                return result;
            }
        }

        return pjp.proceed(pjp.getArgs());
    }

    private Participant enlistParticipant(ProceedingJoinPoint pjp) {

        Transaction transaction = transactionManager.getCurrentTransaction();
        CompensableMethodContext compensableMethodContext = new CompensableMethodContext(pjp, transaction);

        if (compensableMethodContext.getParticipantRole().equals(ParticipantRole.NORMAL)) {
            return null;
        }

        String confirmMethodName = compensableMethodContext.getConfirmMethodName();
        String cancelMethodName = compensableMethodContext.getCancelMethodName();
        Class<? extends TransactionContextEditor> transactionContextEditorClass = compensableMethodContext.getTransactionContextEditorClass();

        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());

        if (compensableMethodContext.getTransactionContext() == null) {
            FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(new TransactionContext(xid, TransactionStatus.TRYING.getId(), ParticipantStatus.TRYING.getId()), pjp.getTarget(), ((MethodSignature) pjp.getSignature()).getMethod(), pjp.getArgs());
        }

        Class targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), compensableMethodContext.getMethod().getName(), compensableMethodContext.getMethod().getParameterTypes());


        InvocationContext confirmInvocation = new InvocationContext(targetClass,
                confirmMethodName,
                compensableMethodContext.getMethod().getParameterTypes(), pjp.getArgs());

        InvocationContext cancelInvocation = new InvocationContext(targetClass,
                cancelMethodName,
                compensableMethodContext.getMethod().getParameterTypes(), pjp.getArgs());

        Participant participant =
                new Participant(
                        xid,
                        confirmInvocation,
                        cancelInvocation,
                        transactionContextEditorClass);

        transactionManager.enlistParticipant(participant);
        return participant;
    }
}
