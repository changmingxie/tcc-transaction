package org.mengyun.tcctransaction.nutz.support;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.common.MethodType;
import org.nutz.aop.InterceptorChain;

/**
 * Created by changmingxie on 11/21/15.
 */
public class NutzSupportUtils {

    public static Method getCompensableMethod(InterceptorChain chain) {
        Method method = chain.getCallingMethod();
        if (method.getAnnotation(Compensable.class) == null) {
            try {
                method = chain.getReturn().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return null;
            }
        } 
        return method;
    }
   
}
