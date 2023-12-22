package org.mengyun.tcctransaction.discovery.registry.zookeeper;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 18:30
 */
public class ZookeeperRegistryProperties {
    private String connectString = "127.0.0.1:2181";
    private int sessionTimeout = 10 * 1000;
    private int connectTimeout = 2 * 1000;
    private String digest;
    private int baseSleepTime = 500;
    private int maxRetries = 3;
    private String backupConnectString;
    private String backupDigest;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public int getBaseSleepTime() {
        return baseSleepTime;
    }

    public void setBaseSleepTime(int baseSleepTime) {
        this.baseSleepTime = baseSleepTime;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getBackupConnectString() {
        return backupConnectString;
    }

    public void setBackupConnectString(String backupConnectString) {
        this.backupConnectString = backupConnectString;
    }

    public String getBackupDigest() {
        return backupDigest;
    }

    public void setBackupDigest(String backupDigest) {
        this.backupDigest = backupDigest;
    }
}
