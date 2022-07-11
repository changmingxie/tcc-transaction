package org.mengyun.tcctransaction.remoting;

import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;

public interface RemotingClient<T> extends RemotingService<T> {

    RemotingCommand invokeSync(final String addr, final RemotingCommand request, final long timeoutMillis);

    void invokeOneway(final String addr, final RemotingCommand request, final long timeoutMillis);
}
