package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.context.ThreadLocalTransactionContextEditor;
import org.mengyun.tcctransaction.transaction.TransactionManager;

import java.lang.reflect.Method;

/**
 * Created by changmingxie on 10/30/15.
 */
@Aspect
public abstract class CompensableTransactionAspect {

    private CompensableTransactionInterceptor compensableTransactionInterceptor = new CompensableTransactionInterceptor();

    public void setTransactionManager(TransactionManager transactionManager) {
        this.compensableTransactionInterceptor.setTransactionManager(transactionManager);
    }

    @Pointcut("@annotation(org.mengyun.tcctransaction.api.Compensable)")
    public void compensableTransactionPointcut() {

    }

    @Around("compensableTransactionPointcut()")
    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        Compensable compensable = method.getAnnotation(Compensable.class);

        return compensableTransactionInterceptor.interceptCompensableMethod(new AspectJTransactionMethodJoinPoint(pjp, compensable, ThreadLocalTransactionContextEditor.class));
    }

    public abstract int getOrder();
}
