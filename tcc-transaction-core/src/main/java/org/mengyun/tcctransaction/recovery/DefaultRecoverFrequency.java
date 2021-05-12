package org.mengyun.tcctransaction.recovery;

/**
 * Created by changming.xie on 6/1/16.
 */
public class DefaultRecoverFrequency implements RecoverFrequency {

    public static final RecoverFrequency INSTANCE = new DefaultRecoverFrequency();

    private int maxRetryCount = 30;

    private int recoverDuration = 30; //30 seconds

    private String cronExpression = "0/15 * * * * ? ";

    private int fetchPageSize = 500;

    private int concurrentRecoveryThreadCount = Runtime.getRuntime().availableProcessors() * 2;

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public int getRecoverDuration() {
        return recoverDuration;
    }

    public void setRecoverDuration(int recoverDuration) {
        this.recoverDuration = recoverDuration;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public int getConcurrentRecoveryThreadCount() {
        return concurrentRecoveryThreadCount;
    }

    public void setConcurrentRecoveryThreadCount(int concurrentRecoveryThreadCount) {
        this.concurrentRecoveryThreadCount = concurrentRecoveryThreadCount;
    }

    public int getFetchPageSize() {
        return fetchPageSize;
    }

    public void setFetchPageSize(int fetchPageSize) {
        this.fetchPageSize = fetchPageSize;
    }
}