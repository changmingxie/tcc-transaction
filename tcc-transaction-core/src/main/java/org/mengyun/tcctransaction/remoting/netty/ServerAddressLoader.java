package org.mengyun.tcctransaction.remoting.netty;

import java.net.InetSocketAddress;
import java.util.List;

public interface ServerAddressLoader {
    InetSocketAddress selectOne(String key);

    List<InetSocketAddress> getAll(String key);

    boolean isAvailableAddress(InetSocketAddress remoteAddress);
}
