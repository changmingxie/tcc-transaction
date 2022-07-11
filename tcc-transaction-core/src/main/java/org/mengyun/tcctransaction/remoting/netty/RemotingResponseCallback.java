package org.mengyun.tcctransaction.remoting.netty;

import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;

public interface RemotingResponseCallback {

    void callback(RemotingCommand response);
}
