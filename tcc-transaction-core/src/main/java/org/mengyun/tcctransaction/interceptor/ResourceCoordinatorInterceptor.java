package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.InvocationContext;
import org.mengyun.tcctransaction.Participant;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionContextEditor;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.common.ParticipantRole;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.ReflectionUtils;

import java.lang.annotation.Annotation;

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

        if (transaction != null) {

            switch (transaction.getStatus()) {
                case TRYING:
                    enlistParticipant(pjp);
                    break;
                case CONFIRMING:
                    break;
                case CANCELLING:
                    break;
            }
        }

        return pjp.proceed(pjp.getArgs());
    }

    private void enlistParticipant(ProceedingJoinPoint pjp) {

        Transaction transaction = transactionManager.getCurrentTransaction();
        CompensableMethodContext compensableMethodContext = new CompensableMethodContext(pjp, transaction);

        if (compensableMethodContext.getParticipantRole().equals(ParticipantRole.NORMAL)) {
            return;
        }

        String confirmMethodName = compensableMethodContext.getConfirmMethodName();
        String cancelMethodName = compensableMethodContext.getCancelMethodName();
        Class<? extends TransactionContextEditor> transactionContextEditorClass = compensableMethodContext.getTransactionContextEditorClass();

        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());

        if (compensableMethodContext.getTransactionContext() == null) {
            FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(new TransactionContext(xid, TransactionStatus.TRYING.getId()), pjp.getTarget(), ((MethodSignature) pjp.getSignature()).getMethod(), pjp.getArgs());
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
    }
}
