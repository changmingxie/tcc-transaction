package org.mengyun.tcctransaction.dashboard.config;

import org.mengyun.tcctransaction.dashboard.enums.ConnectionMode;

/**
 * @Author huabao.fang
 * @Date 2022/6/12 00:32
 **/
public class DashboardProperties {
    private String userName;
    private String password;
    private ConnectionMode connectionMode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnectionMode getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(ConnectionMode connectionMode) {
        this.connectionMode = connectionMode;
    }
}
