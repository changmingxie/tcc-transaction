package org.mengyun.tcctransaction.properties.remoting;

import org.mengyun.tcctransaction.remoting.netty.NettyConfig;

/**
 * @author Nervose.Wu
 * @date 2022/5/31 16:11
 */
public class NettyProperties implements NettyConfig {

    private int workerThreadSize = Runtime.getRuntime().availableProcessors() * 2;
    private int socketBacklog = 512;
    private int socketRcvBufSize = 153600;
    private int socketSndBufSize = 153600;
    private int frameMaxLength = 1024 * 1024 * 2;
    private int workSelectorThreadSize = Runtime.getRuntime().availableProcessors();

    @Override
    public int getWorkSelectorThreadSize() {
        return workSelectorThreadSize;
    }

    public void setWorkSelectorThreadSize(int workSelectorThreadSize) {
        this.workSelectorThreadSize = workSelectorThreadSize;
    }

    @Override
    public int getWorkerThreadSize() {
        return workerThreadSize;
    }

    public void setWorkerThreadSize(int workerThreadSize) {
        this.workerThreadSize = workerThreadSize;
    }

    @Override
    public int getSocketBacklog() {
        return socketBacklog;
    }

    public void setSocketBacklog(int socketBacklog) {
        this.socketBacklog = socketBacklog;
    }

    @Override
    public int getSocketRcvBufSize() {
        return socketRcvBufSize;
    }

    public void setSocketRcvBufSize(int socketRcvBufSize) {
        this.socketRcvBufSize = socketRcvBufSize;
    }

    @Override
    public int getSocketSndBufSize() {
        return socketSndBufSize;
    }

    public void setSocketSndBufSize(int socketSndBufSize) {
        this.socketSndBufSize = socketSndBufSize;
    }

    @Override
    public int getFrameMaxLength() {
        return frameMaxLength;
    }

    public void setFrameMaxLength(int frameMaxLength) {
        this.frameMaxLength = frameMaxLength;
    }
}
