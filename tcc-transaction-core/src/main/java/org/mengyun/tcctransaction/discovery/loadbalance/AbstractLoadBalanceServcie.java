package org.mengyun.tcctransaction.discovery.loadbalance;

import org.mengyun.tcctransaction.utils.CollectionUtils;

import java.util.List;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 16:42
 */
public abstract class AbstractLoadBalanceServcie implements LoadBalanceServcie {

    @Override
    public <T> T select(List<T> invokers) {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return doSelect(invokers);
    }

    protected abstract <T> T doSelect(List<T> invokers);
}
