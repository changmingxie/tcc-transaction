package org.mengyun.tcctransaction.discovery.registry.nacos;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 18:31
 */

public class NacosRegistryProperties {
    private String serverAddr = "127.0.0.1:8848";
    private String namespace = "public";
    private String group = "TCC_GROUP";
    private String username = "nacos";
    private String password = "nacos";
    private String serviceName = "tcc-server";

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
