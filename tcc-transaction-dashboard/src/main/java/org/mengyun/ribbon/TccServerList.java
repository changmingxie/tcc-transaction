package org.mengyun.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;
import com.netflix.loadbalancer.Server;
import org.mengyun.tcctransaction.discovery.registry.RegistryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nervose.Wu
 * @date 2023/2/9 15:32
 */
public class TccServerList<T extends Server> extends AbstractServerList<T> {

    private RegistryService registryService;

    public TccServerList(RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }

    @Override
    public List<T> getInitialListOfServers() {
        return getServers();
    }

    @Override
    public List<T> getUpdatedListOfServers() {
        return getServers();
    }

    private List<T> getServers() {
        return (List<T>) registryService.lookup(true)
                .stream()
                .map(each -> new Server(each.substring(0, each.indexOf(':')), Integer.parseInt(each.substring(each.indexOf(':') + 1))))
                .collect(Collectors.toList());
    }
}
