package org.mengyun.tcctransaction.properties.store;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;

/**
 * @author Nervose.Wu
 * @date 2022/5/25 17:38
 */
public class JdbcStoreProperties {

    private String driverClass = "com.mysql.jdbc.Driver";
    private String username = "root";
    private String password = "welcome1";
    private String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/TCC?useSSL=false";
    private int initialPoolSize = 10;
    private int minPoolSize = 10;
    private int maxPoolSize = 50;
    private int checkoutTimeout = 2000;
    private DataSource instance;

    public DataSource getDataSource() {
        if (instance == null) {
            try {
                ComboPooledDataSource dataSource = new ComboPooledDataSource();
                dataSource.setDriverClass(driverClass);
                dataSource.setUser(username);
                dataSource.setPassword(password);
                dataSource.setJdbcUrl(jdbcUrl);
                dataSource.setInitialPoolSize(initialPoolSize);
                dataSource.setMinPoolSize(minPoolSize);
                dataSource.setMaxPoolSize(maxPoolSize);
                dataSource.setCheckoutTimeout(checkoutTimeout);
                instance = dataSource;
            } catch (Exception e) {
                throw new RuntimeException("failed to create dataSource", e);
            }
        }
        return instance;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
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

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getCheckoutTimeout() {
        return checkoutTimeout;
    }

    public void setCheckoutTimeout(int checkoutTimeout) {
        this.checkoutTimeout = checkoutTimeout;
    }
}
