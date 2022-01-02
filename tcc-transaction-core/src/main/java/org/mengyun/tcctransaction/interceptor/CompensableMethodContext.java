package org.mengyun.tcctransaction.interceptor;

import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionContextEditor;
import org.mengyun.tcctransaction.api.UniqueIdentity;
import org.mengyun.tcctransaction.common.ParticipantRole;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by changming.xie on 04/04/19.
 */
public class CompensableMethodContext {

    TransactionMethodJoinPoint pjp = null;

    private Transaction transaction = null;

    TransactionContext transactionContext = null;

    Compensable compensable = null;

    public CompensableMethodContext(TransactionMethodJoinPoint pjp, Transaction transaction) {
        this.pjp = pjp;

        this.transaction = transaction;

        this.compensable = pjp.getCompensable();

        this.transactionContext = FactoryBuilder.factoryOf(pjp.getTransactionContextEditorClass()).getInstance().get(pjp.getTarget(), pjp.getMethod(), pjp.getArgs());
    }

    public Compensable getAnnotation() {
        return compensable;
    }

    public TransactionContext getTransactionContext() {
        return transactionContext;
    }

    public Method getMethod() {
        return pjp.getMethod();
    }

    public Object getUniqueIdentity() {
        Annotation[][] annotations = this.getMethod().getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation.annotationType().equals(UniqueIdentity.class)) {

                    Object[] params = pjp.getArgs();
                    Object unqiueIdentity = params[i];

                    return unqiueIdentity;
                }
            }
        }

        return null;
    }

    public ParticipantRole getParticipantRole() {

        //1. If method is @Compensable annotated, which means need tcc transaction, if no active transaction, need require new.
        //2. If method is not @Compensable annotated, but with TransactionContext Param.
        //   It means need participant tcc transaction if has active transaction. If transactionContext is null, then it enlist the transaction as CONSUMER role,
        //   else means there is another method roled as Consumer has enlisted the transaction, this method no need enlist.


        //Method is @Compensable annotated. Currently has no active transaction && no active transaction context,
        // then the method need enlist the transaction as ROOT role.
        if (compensable != null && transaction == null && transactionContext == null) {
            return ParticipantRole.ROOT;
        }


        //Method is @Compensable annotated. Currently has no active transaction, but has active transaction context.
        // This means there is a active transaction, need renew the transaction and enlist the transaction as PROVIDER role.
        if (compensable != null && transaction == null && transactionContext != null) {
            return ParticipantRole.PROVIDER;
        }

        //Method is @Compensable annotated, and has active transaction, but no transaction context.
        //then the method need enlist the transaction as CONSUMER role,
        // its role may be ROOT before if this method is the entrance of the tcc transaction.
        if (compensable != null && transaction != null && transactionContext == null) {
            return ParticipantRole.CONSUMER;
        }

        //Method is @Compensable annotated, and has active transaction, and also has transaction context.
        //then the method need enlist the transaction as CONSUMER role, its role maybe PROVIDER before.
        if (compensable != null && transaction != null && transactionContext != null) {
            return ParticipantRole.CONSUMER;
        }

        //Method is not @Compensable annotated, but with TransactionContext Param.
        // If currently there is a active transaction,
        // then need enlist the transaction with CONSUMER role.
        if (compensable == null && transaction != null) {
            return ParticipantRole.CONSUMER;
        }

        return ParticipantRole.NORMAL;
    }


    public Object proceed() throws Throwable {
        return this.pjp.proceed();
    }

    public Class<? extends TransactionContextEditor> getTransactionContextEditorClass() {
        return pjp.getTransactionContextEditorClass();
    }

    public String getConfirmMethodName() {
        return compensable == null ? pjp.getMethod().getName() : compensable.confirmMethod();
    }

    public String getCancelMethodName() {
        return compensable == null ? pjp.getMethod().getName() : compensable.cancelMethod();
    }
}