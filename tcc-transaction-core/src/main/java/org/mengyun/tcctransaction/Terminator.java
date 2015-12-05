package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by changmingxie on 10/30/15.
 */
public class Terminator implements Serializable {

    private InvocationContext confirmInvocationContext;

    private InvocationContext cancelInvocationContext;

    public Terminator(InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext) {
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
    }

    public void commit() {

        try {
            invoke(confirmInvocationContext);
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }
    }

    public void rollback() {
        try {
            invoke(cancelInvocationContext);
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }
    }

    private Object invoke(InvocationContext invocationContext) {

        if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {
            try {

                Object target = BeanFactoryAdapter.getBean(invocationContext.getTargetClass());

                if (target == null && !invocationContext.getTargetClass().isInterface()) {
                    target = invocationContext.getTargetClass().newInstance();
                }

                Method method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());
                return method.invoke(target, invocationContext.getArgs());

            } catch (Throwable e) {
                throw new Error(e);
            }
        }
        return null;
    }
}
