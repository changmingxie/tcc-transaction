package org.mengyun.tcctransaction.transaction;

public class Invocation {

    private String methodName;

    private InvocationContext invocationContext;

    public Invocation(String methodName, InvocationContext invocationContext) {
        this.methodName = methodName;
        this.invocationContext = invocationContext;
    }

    public Object[] getArgs() {
        return invocationContext.getArgs();
    }

    public Class getTargetClass() {
        return invocationContext.getTargetClass();
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getParameterTypes() {
        return invocationContext.getParameterTypes();
    }
}
