package org.mengyun.tcctransaction.recovery;


import java.util.Map;
import java.util.Set;

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

    String getCustomConnectionProviderClassName();

    Map<String, String> getCustomConnectionProviderProperties();

    boolean isEnableDelayCancel();

    Set<Class<? extends Exception>> getDelayCancelExceptions();

    int getMaxTimeTreatTryingAsFailed();
}
