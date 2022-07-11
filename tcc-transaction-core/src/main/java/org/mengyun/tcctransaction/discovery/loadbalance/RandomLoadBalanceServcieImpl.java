package org.mengyun.tcctransaction.discovery.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 16:43
 */
public class RandomLoadBalanceServcieImpl extends AbstractLoadBalanceServcie {

    @Override
    protected <T> T doSelect(List<T> invokers) {
        int length = invokers.size();
        return invokers.get(ThreadLocalRandom.current().nextInt(length));
    }
}
