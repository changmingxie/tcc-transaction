package org.mengyun.tcctransaction.transaction;

import java.io.Serializable;

/**
 * Created by changmingxie on 11/9/15.
 */
public class InvocationContext implements Serializable {

    private static final long serialVersionUID = -7969140711432461165L;

    private Class targetClass;

    private String confirmMethodName;

    private String cancelMethodName;

    private Class[] parameterTypes;

    private Object[] args;

    public InvocationContext() {
    }

    public InvocationContext(Class targetClass, String confirmMethodName, String cancelMethodName, Class[] parameterTypes, Object... args) {
        this.confirmMethodName = confirmMethodName;
        this.cancelMethodName = cancelMethodName;
        this.parameterTypes = parameterTypes;
        this.targetClass = targetClass;
        this.args = args;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public String getConfirmMethodName() {
        return confirmMethodName;
    }

    public void setConfirmMethodName(String confirmMethodName) {
        this.confirmMethodName = confirmMethodName;
    }

    public String getCancelMethodName() {
        return cancelMethodName;
    }

    public void setCancelMethodName(String cancelMethodName) {
        this.cancelMethodName = cancelMethodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
