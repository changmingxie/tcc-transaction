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
 * Created by changmingxie on 11/8/15.
 */
@Aspect
public abstract class ResourceCoordinatorAspect {

    private ResourceCoordinatorInterceptor resourceCoordinatorInterceptor = new ResourceCoordinatorInterceptor();

    @Pointcut("@annotation(org.mengyun.tcctransaction.api.Compensable) || @annotation(org.mengyun.tcctransaction.api.EnableTcc)")
    public void transactionResourcePointcut() {

    }

    @Around("transactionResourcePointcut()")
    public Object interceptTransactionResourceMethodWithCompensableAnnotation(ProceedingJoinPoint pjp) throws Throwable {

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        Compensable compensable = method.getAnnotation(Compensable.class);

        return interceptTransactionContextMethod(new AspectJTransactionMethodJoinPoint(pjp, compensable, ThreadLocalTransactionContextEditor.class));
    }

    public Object interceptTransactionContextMethod(TransactionMethodJoinPoint pjp) throws Throwable {
        return resourceCoordinatorInterceptor.interceptTransactionContextMethod(pjp);
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.resourceCoordinatorInterceptor.setTransactionManager(transactionManager);
    }

    public abstract int getOrder();
}
