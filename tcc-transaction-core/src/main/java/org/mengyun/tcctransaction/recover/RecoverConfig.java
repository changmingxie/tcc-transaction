package org.mengyun.tcctransaction.recover;

import java.util.List;

/**
 * Created by changming.xie on 6/1/16.
 */
public interface RecoverConfig {

    public int getMaxRetryCount();

    public int getRecoverDuration();

    public String getCronExpression();

    public List<Class<? extends Exception>> getDelayCancelExceptions();

    public void setDelayCancelExceptions(List<Class<? extends Exception>> delayRecoverExceptions);
}
