package org.mengyun.tcctransaction.discovery.loadbalance;

import org.mengyun.tcctransaction.discovery.registry.ClientRegistryConfig;
import org.mengyun.tcctransaction.load.LoadUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 17:18
 */
public class LoadBalanceFactory {

    private static final Map<String, LoadBalanceProvider> CANDIDATE_LOAD_BALANCES = new HashMap<>();

    private LoadBalanceFactory(){
    }

    static {
        ServiceLoader.load(LoadBalanceProvider.class)
                .forEach(each -> CANDIDATE_LOAD_BALANCES.put(LoadUtils.getServiceName(each.getClass()), each));
    }

    public static LoadBalanceServcie getInstance(ClientRegistryConfig clientRegistryConfig) {
        String loadBalanceType = clientRegistryConfig.getLoadBalanceType();
        if (!CANDIDATE_LOAD_BALANCES.containsKey(loadBalanceType)) {
            throw new IllegalArgumentException("invalid loadBalanceType: " + loadBalanceType);
        }
        return CANDIDATE_LOAD_BALANCES.get(loadBalanceType).provide();

    }
}
