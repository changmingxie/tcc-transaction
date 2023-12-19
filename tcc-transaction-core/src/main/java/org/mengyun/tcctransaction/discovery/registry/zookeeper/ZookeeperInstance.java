package org.mengyun.tcctransaction.discovery.registry.zookeeper;

import org.apache.curator.framework.CuratorFramework;

/**
 * @author Nervose.Wu
 * @date 2023/2/7 14:31
 */
public class ZookeeperInstance {
    private String connectString;
    private String digest;
    private final CuratorFramework curator;
    private String registeredPath;
    private String registeredPathForDashboard;

    public ZookeeperInstance(String connectString, String digest, CuratorFramework curator) {
        this.connectString = connectString;
        this.digest = digest;
        this.curator = curator;
    }


    public CuratorFramework getCurator() {
        return curator;
    }

    public String getRegisteredPath() {
        return registeredPath;
    }

    public void setRegisteredPath(String registeredPath) {
        this.registeredPath = registeredPath;
    }

    public String getRegisteredPathForDashboard() {
        return registeredPathForDashboard;
    }

    public void setRegisteredPathForDashboard(String registeredPathForDashboard) {
        this.registeredPathForDashboard = registeredPathForDashboard;
    }

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
