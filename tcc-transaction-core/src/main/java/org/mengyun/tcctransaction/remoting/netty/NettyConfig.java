package org.mengyun.tcctransaction.remoting.netty;

public interface NettyConfig {

    int getWorkSelectorThreadSize();

    int getWorkerThreadSize();

    int getSocketBacklog();

    int getSocketRcvBufSize();

    int getSocketSndBufSize();

    int getFrameMaxLength();
}
