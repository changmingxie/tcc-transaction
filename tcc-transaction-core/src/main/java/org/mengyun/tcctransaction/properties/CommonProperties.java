package org.mengyun.tcctransaction.properties;

/**
 * @author Nervose.Wu
 * @date 2022/5/24 20:20
 */
public class CommonProperties {

    private int requestProcessThreadSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private int requestProcessThreadQueueCapacity = 1024;

    public int getRequestProcessThreadSize() {
        return requestProcessThreadSize;
    }

    public void setRequestProcessThreadSize(int requestProcessThreadSize) {
        this.requestProcessThreadSize = requestProcessThreadSize;
    }

    public int getRequestProcessThreadQueueCapacity() {
        return requestProcessThreadQueueCapacity;
    }

    public void setRequestProcessThreadQueueCapacity(int requestProcessThreadQueueCapacity) {
        this.requestProcessThreadQueueCapacity = requestProcessThreadQueueCapacity;
    }
}
