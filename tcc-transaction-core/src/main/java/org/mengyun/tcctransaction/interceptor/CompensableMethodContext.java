package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.api.*;
import org.mengyun.tcctransaction.common.ParticipantRole;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by changming.xie on 04/04/19.
 */
public class CompensableMethodContext {

    ProceedingJoinPoint pjp = null;
    Method method = null;

    Compensable compensable = null;
    Propagation propagation = null;

    TransactionContext transactionContext = null;
    Class<? extends TransactionContextEditor> transactionContextEditorClass;
    String confirmMethodName = null;
    String cancelMethodName = null;

    private Transaction transaction = null;

    public CompensableMethodContext(ProceedingJoinPoint pjp, Transaction transaction) {

        this.pjp = pjp;

        this.method = getCompensableMethod();

        if (method == null) {
            throw new RuntimeException(String.format("join point not found method, point is : %s", pjp.getSignature().getName()));
        }

        this.compensable = method.getAnnotation(Compensable.class);

        if (this.compensable != null) {
            this.propagation = compensable.propagation();
            transactionContextEditorClass = compensable.transactionContextEditor();
            confirmMethodName = this.compensable.confirmMethod();
            cancelMethodName = this.compensable.cancelMethod();
        } else {
            transactionContextEditorClass = Compensable.DefaultTransactionContextEditor.class;
            confirmMethodName = this.method.getName();
            cancelMethodName = this.method.getName();
        }

        this.transactionContext = FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().get(pjp.getTarget(), method, pjp.getArgs());

        this.transaction = transaction;
    }

    public Compensable getAnnotation() {
        return compensable;
    }

    public Propagation getPropagation() {
        return propagation;
    }

    public TransactionContext getTransactionContext() {
        return transactionContext;
    }

    public Method getMethod() {
        return method;
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
        if(compensable != null && transaction != null && transactionContext != null) {
            return ParticipantRole.CONSUMER;
        }

        //Method is not @Compensable annotated, but with TransactionContext Param.
        // If currently there is a active transaction and transaction context is null,
        // then need enlist the transaction with CONSUMER role.
        if (compensable == null && transaction != null && transactionContext == null) {
            return ParticipantRole.CONSUMER;
        }

        return ParticipantRole.NORMAL;
    }


    private Method getCompensableMethod() {

        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

        Method foundMethod = null;

        //first find if exist @Compensable
        if (method.getAnnotation(Compensable.class) != null) {
            foundMethod = method;
        } else {

            Method targetMethod = null;
            try {
                targetMethod = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                targetMethod = null;
            }

            if (targetMethod != null && targetMethod.getAnnotation(Compensable.class) != null) {
                foundMethod = targetMethod;
            } else {

                if (Compensable.DefaultTransactionContextEditor.getTransactionContextParamPosition(method.getParameterTypes()) >= 0) {
                    foundMethod = method;
                }
            }
        }

        return foundMethod;
    }

    public Object proceed() throws Throwable {
        return this.pjp.proceed();
    }

    public Class<? extends TransactionContextEditor> getTransactionContextEditorClass() {
        return transactionContextEditorClass;
    }

    public String getConfirmMethodName() {
        return confirmMethodName;
    }

    public String getCancelMethodName() {
        return cancelMethodName;
    }
}