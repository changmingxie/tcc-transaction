package org.mengyun.tcctransaction.discovery.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 16:41
 */
public class RoundRobinLoadBalanceServcieImpl extends AbstractLoadBalanceServcie {

    private final AtomicInteger sequence = new AtomicInteger();

    @Override
    protected <T> T doSelect(List<T> invokers) {
        return invokers.get(getPositiveSequence() % invokers.size());
    }

    private int getPositiveSequence() {
        for (; ; ) {
            int current = sequence.get();
            int next = current == Integer.MAX_VALUE ? 0 : current + 1;
            if (sequence.compareAndSet(current, next)) {
                return current;
            }
        }
    }
}
