package org.mengyun.tcctransaction.discovery.loadbalance;

import org.mengyun.tcctransaction.load.LoadInfo;

/**
 * @author Nervose.Wu
 * @date 2022/5/19 14:48
 */
@LoadInfo(name = "Random")
public class RandomLoadBalanceProvider implements LoadBalanceProvider {

    @Override
    public LoadBalanceServcie provide() {
        return new RandomLoadBalanceServcieImpl();
    }
}
