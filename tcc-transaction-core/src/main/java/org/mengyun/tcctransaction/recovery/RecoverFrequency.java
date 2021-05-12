package org.mengyun.tcctransaction.recovery;


/**
 * Created by changming.xie on 6/1/16.
 */
public interface RecoverFrequency {

    int getMaxRetryCount();

    int getFetchPageSize();

    int getRecoverDuration();

    String getCronExpression();

    int getConcurrentRecoveryThreadCount();
}
