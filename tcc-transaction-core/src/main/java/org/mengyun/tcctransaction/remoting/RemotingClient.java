package org.mengyun.tcctransaction.remoting;

import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;

public interface RemotingClient<T> extends RemotingService<T> {

    RemotingCommand invokeSync(final RemotingCommand request, final long timeoutMillis);

    RemotingCommand invokeSync(final String address, final RemotingCommand request, final long timeoutMillis);

    void invokeOneway(final RemotingCommand request, final long timeoutMillis);


    void invokeOneway(final String address, final RemotingCommand request, final long timeoutMillis);
}
