package org.mengyun.tcctransaction.remoting;

import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;

public interface RequestProcessor<T> {

    RemotingCommand processRequest(T context, RemotingCommand request);
}
