package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.ParameterTransactionContextEditor;

/**
 * Created by changmingxie on 11/8/15.
 */
@Aspect
public abstract class ResourceCoordinatorAspect {

    private ResourceCoordinatorInterceptor resourceCoordinatorInterceptor;

    @Pointcut("@annotation(org.mengyun.tcctransaction.api.Compensable) || execution(* *(org.mengyun.tcctransaction.api.TransactionContext,..))")
    public void transactionResourcePointcut() {

    }


    @Around("transactionResourcePointcut()")
    public Object interceptTransactionResourceMethodWithCompensableAnnotation(ProceedingJoinPoint pjp) throws Throwable {
        Compensable compensable = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(Compensable.class);
        if (compensable != null) {
            return interceptTransactionContextMethod(new AspectJTransactionMethodJoinPoint(pjp, compensable, compensable.transactionContextEditor()));
        } else {
            return interceptTransactionContextMethod(new AspectJTransactionMethodJoinPoint(pjp, null, ParameterTransactionContextEditor.class));
        }
    }

    public Object interceptTransactionContextMethod(TransactionMethodJoinPoint pjp) throws Throwable {
        return resourceCoordinatorInterceptor.interceptTransactionContextMethod(pjp);
    }

    public void setResourceCoordinatorInterceptor(ResourceCoordinatorInterceptor resourceCoordinatorInterceptor) {
        this.resourceCoordinatorInterceptor = resourceCoordinatorInterceptor;
    }

    public abstract int getOrder();
}
