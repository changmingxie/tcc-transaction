package org.mengyun.tcctransaction.interceptor;

import org.mengyun.tcctransaction.api.ParticipantStatus;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.context.TransactionContextEditor;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.transaction.InvocationContext;
import org.mengyun.tcctransaction.transaction.Participant;
import org.mengyun.tcctransaction.transaction.Transaction;
import org.mengyun.tcctransaction.transaction.TransactionManager;
import org.mengyun.tcctransaction.xid.TransactionXid;

/**
 * Created by changmingxie on 11/8/15.
 */
public class ResourceCoordinatorInterceptor {

    private TransactionManager transactionManager;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object interceptTransactionContextMethod(TransactionMethodJoinPoint pjp) throws Throwable {

        Transaction transaction = transactionManager.getCurrentTransaction();

        if (transaction != null && transaction.getStatus().equals(TransactionStatus.TRYING)) {

            Participant participant = enlistParticipant(pjp);

            if (participant != null) {

                Object result = null;
                TransactionContext transactionContext = new TransactionContext(transaction.getRootDomain(), transaction.getRootXid(), participant.getXid(), TransactionStatus.TRYING.getId(), ParticipantStatus.TRYING.getId());
                FactoryBuilder.factoryOf(participant.getTransactionContextEditorClass()).getInstance().set(transactionContext, pjp.getTarget(), pjp.getMethod(), pjp.getArgs());
                try {
                    result = pjp.proceed(pjp.getArgs());
                    participant.setStatus(ParticipantStatus.TRY_SUCCESS);
                } catch (Throwable e) {
                    participant.setStatus(ParticipantStatus.TRY_FAILED);

                    //if root transaction, here no need persistent transaction
                    // because following stage is rollback, transaction's status is changed to CANCELING and save
//                    transactionManager.update(participant);
//
                    throw e;
                } finally {
                    FactoryBuilder.factoryOf(participant.getTransactionContextEditorClass()).getInstance().clear(transactionContext, pjp.getTarget(), pjp.getMethod(), pjp.getArgs());
                }


                return result;
            }
        }

        return pjp.proceed(pjp.getArgs());
    }

    private Participant enlistParticipant(TransactionMethodJoinPoint pjp) {

        Transaction transaction = transactionManager.getCurrentTransaction();
        CompensableMethodContext compensableMethodContext = new CompensableMethodContext(pjp, transaction);

        String confirmMethodName = compensableMethodContext.getConfirmMethodName();
        String cancelMethodName = compensableMethodContext.getCancelMethodName();
        Class<? extends TransactionContextEditor> transactionContextEditorClass = compensableMethodContext.getTransactionContextEditorClass();
        TransactionXid xid = TransactionXid.withUniqueIdentity(null);

        Class targetClass = compensableMethodContext.getDeclaredClass();

        InvocationContext invocationContext = new InvocationContext(targetClass,
                confirmMethodName,
                cancelMethodName,
                compensableMethodContext.getMethod().getParameterTypes(), compensableMethodContext.getArgs());

        Participant participant =
                new Participant(
                        transaction.getRootDomain(),
                        transaction.getRootXid(),
                        xid,
                        invocationContext,
                        transactionContextEditorClass);

        transactionManager.enlistParticipant(participant);
        return participant;
    }
}
