package org.mengyun.tcctransaction.discovery.registry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 16:27
 */
public interface RegistryService {

    void start();

    void register(InetSocketAddress address, InetSocketAddress addressForDashboard);

    void subscribe(boolean addressForDashboard);

    List<String> lookup(boolean addressForDashboard);

    void close();
}
