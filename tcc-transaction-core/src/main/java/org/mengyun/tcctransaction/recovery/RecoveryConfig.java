package org.mengyun.tcctransaction.recovery;


/**
 * Created by changming.xie on 6/1/16.
 */
public interface RecoveryConfig {

    boolean isRecoveryEnabled();

    int getMaxRetryCount();

    int getRecoverDuration();

    String getCronExpression();

    int getConcurrentRecoveryThreadCount();

    int getFetchPageSize();

    boolean isUpdateJobForcibly();

    String getQuartzDataSourceDriver();

    String getQuartzDataSourceUrl();

    String getQuartzDataSourceUser();

    String getQuartzDataSourcePassword();

    String getQuartzDataSourceValidationQuery();

    int getQuartzDataSourceCheckoutTimeout();

    int getQuartzDataSourceInitialPoolSize();

    int getQuartzDataSourceMinPoolSize();

    int getQuartzDataSourceMaxPoolSize();

    int getQuartzThreadPoolThreadCount();

    boolean isQuartzClustered();
}
