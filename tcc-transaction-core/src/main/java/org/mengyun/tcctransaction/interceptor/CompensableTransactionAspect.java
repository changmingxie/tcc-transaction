package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.NullableTransactionContextEditor;
import org.mengyun.tcctransaction.api.ParameterTransactionContextEditor;
import org.mengyun.tcctransaction.api.TransactionContextEditor;

import java.lang.reflect.Method;

/**
 * Created by changmingxie on 10/30/15.
 */
@Aspect
public abstract class CompensableTransactionAspect {

    private CompensableTransactionInterceptor compensableTransactionInterceptor;

    public void setCompensableTransactionInterceptor(CompensableTransactionInterceptor compensableTransactionInterceptor) {
        this.compensableTransactionInterceptor = compensableTransactionInterceptor;
    }

    @Pointcut("@annotation(org.mengyun.tcctransaction.api.Compensable)")
    public void compensableTransactionPointcut() {

    }

    @Around("compensableTransactionPointcut()")
    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        Compensable compensable = method.getAnnotation(Compensable.class);

        Class<? extends TransactionContextEditor> transactionContextEditor = NullableTransactionContextEditor.class;

        if (compensable != null) {
            transactionContextEditor = compensable.transactionContextEditor();
        }

        if (transactionContextEditor.equals(NullableTransactionContextEditor.class)
                && ParameterTransactionContextEditor.hasTransactionContextParameter(method.getParameterTypes())) {

            transactionContextEditor = ParameterTransactionContextEditor.class;
        }

        return compensableTransactionInterceptor.interceptCompensableMethod(new AspectJTransactionMethodJoinPoint(pjp, compensable, transactionContextEditor));
    }

    public abstract int getOrder();
}
