package org.mengyun.tcctransaction.recovery;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.quartz.utils.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nervose.Wu
 * @date 2022/7/7 10:42
 */
public class ReusableConnectionProvider implements ConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(ReusableConnectionProvider.class.getSimpleName());
    private static volatile ComboPooledDataSource instance;
    private static AtomicInteger inuseCounter = new AtomicInteger(0);
    private String driver;
    private String URL;
    private String user = "";
    private String password = "";
    private int initialPoolSize = 1;
    private int minPoolSize = 1;
    private int maxPoolSize = 10;
    private int maxStatementsPerConnection = 120;
    private int idleValidationSeconds = 50;
    private int maxIdleSeconds = 0;
    private int checkoutTimeout = 2000;
    private boolean validateOnCheckout = false;
    private String validationQuery;

    public ReusableConnectionProvider() {
        logger.debug("one user joined, current inuse counter:{}", inuseCounter.incrementAndGet());
    }

    @Override
    public void initialize() throws SQLException {
        if (instance == null) {
            synchronized (ReusableConnectionProvider.class) {
                if (instance == null) {
                    ComboPooledDataSource dataSource = null;
                    try {
                        dataSource = new ComboPooledDataSource();

                        dataSource.setDriverClass(driver);
                        dataSource.setUser(user);
                        dataSource.setPassword(password);
                        dataSource.setJdbcUrl(URL);
                        dataSource.setInitialPoolSize(initialPoolSize);
                        dataSource.setMinPoolSize(minPoolSize);
                        dataSource.setMaxPoolSize(maxPoolSize);
                        dataSource.setMaxIdleTime(maxIdleSeconds);
                        dataSource.setMaxStatementsPerConnection(maxStatementsPerConnection);
                        dataSource.setCheckoutTimeout(checkoutTimeout);
                        if (validationQuery != null) {
                            dataSource.setPreferredTestQuery(validationQuery);
                            if (validateOnCheckout) {
                                dataSource.setTestConnectionOnCheckout(true);
                            } else {
                                dataSource.setTestConnectionOnCheckin(true);
                            }
                            dataSource.setIdleConnectionTestPeriod(idleValidationSeconds);
                        }

                        instance = dataSource;
                    } catch (Exception e) {
                        if (dataSource != null) {
                            dataSource.close();
                        }
                        instance = null;
                        throw new SQLException("failed to create dataSource", e);
                    }
                }
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return instance != null ? instance.getConnection() : null;
    }

    @Override
    public void shutdown() throws SQLException {
        logger.debug("one user quit, current inuse counter:{}", inuseCounter.decrementAndGet());
        if (inuseCounter.get() == 0 && instance != null) {
            synchronized (ReusableConnectionProvider.class) {
                if (inuseCounter.get() == 0 && instance != null) {
                    logger.info("no user in use,close the dataSource");
                    instance.close();
                    instance = null;
                }
            }
        }
    }

    /**
     * called by quartz
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
        this.maxStatementsPerConnection = maxStatementsPerConnection;
    }

    public void setIdleValidationSeconds(int idleValidationSeconds) {
        this.idleValidationSeconds = idleValidationSeconds;
    }

    public void setMaxIdleSeconds(int maxIdleSeconds) {
        this.maxIdleSeconds = maxIdleSeconds;
    }

    public void setCheckoutTimeout(int checkoutTimeout) {
        this.checkoutTimeout = checkoutTimeout;
    }

    public void setValidateOnCheckout(boolean validateOnCheckout) {
        this.validateOnCheckout = validateOnCheckout;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }
}
