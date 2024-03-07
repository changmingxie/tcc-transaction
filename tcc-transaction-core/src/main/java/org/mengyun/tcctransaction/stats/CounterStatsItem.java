package org.mengyun.tcctransaction.stats;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Nervose.Wu
 * @date 2024/1/19 14:14
 */
public class CounterStatsItem extends StatsItem {
    private final LongAdder count;

    public CounterStatsItem(String statsName, String statsKey) {
        super(statsName, statsKey);
        this.count = new LongAdder();
    }

    public void record(long inc) {
        count.add(inc);
    }

    public long getCount() {
        return count.sum();
    }
}
