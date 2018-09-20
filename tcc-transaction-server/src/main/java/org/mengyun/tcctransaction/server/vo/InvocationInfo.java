package org.mengyun.tcctransaction.server.vo;

import com.alibaba.fastjson.JSONObject;

public class InvocationInfo {
    private String packageName;
    private String className;
    private String methodName;

    public InvocationInfo(JSONObject obj) {
        className = obj.getString("targetClass");
        int i = className.lastIndexOf('.');
        packageName = className.substring(0, i);
        className = className.substring(i+1);
        methodName = obj.getString("methodName");
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
