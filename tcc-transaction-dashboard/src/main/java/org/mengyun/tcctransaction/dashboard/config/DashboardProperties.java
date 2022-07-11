package org.mengyun.tcctransaction.dashboard.config;

import org.mengyun.tcctransaction.dashboard.enums.DataFetchType;

/**
 * @Author huabao.fang
 * @Date 2022/6/12 00:32
 **/
public class DashboardProperties {
    private String userName;
    private String password;
    private DataFetchType dataFetchType;

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

    public DataFetchType getDataFetchType() {
        return dataFetchType;
    }

    public void setDataFetchType(DataFetchType dataFetchType) {
        this.dataFetchType = dataFetchType;
    }
}
