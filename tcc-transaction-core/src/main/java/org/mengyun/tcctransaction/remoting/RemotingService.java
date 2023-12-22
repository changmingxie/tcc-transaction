package org.mengyun.tcctransaction.remoting;

import io.netty.channel.ChannelHandler;

import java.util.concurrent.ExecutorService;

public interface RemotingService<T> {

    void start();

    void shutdown();

    void registerDefaultProcessor(final RequestProcessor<T> processor, final ExecutorService executor);

    void registerProcessor(final int requestCode, final RequestProcessor<T> processor,
                           final ExecutorService executor);

    void registerChannelHandlers(ChannelHandler... channelHandlers);
}
