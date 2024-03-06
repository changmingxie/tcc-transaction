package org.mengyun.tcctransaction.stats;

import java.util.Arrays;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Nervose.Wu
 * @date 2024/1/29 11:14
 */
public class HistogramStatsItem extends StatsItem {
    private final LongAdder sum;

    private final long[] buckets;
    private final LongAdder[] distribution;


    public HistogramStatsItem(String statsName, String statsKey, long[] buckets) {
        super(statsName, statsKey);
        this.buckets = buckets;
        this.sum = new LongAdder();
        this.distribution = new LongAdder[buckets.length];
        Arrays.setAll(this.distribution, key -> new LongAdder());
    }

    public void record(long inc) {
        for (int i = 0; i < buckets.length; i++) {
            if (inc <= buckets[i]) {
                distribution[i].add(1);
                break;
            }
        }
        sum.add(inc);
    }

    public long getSum() {
        return sum.sum();
    }

    public long[] getDistribution() {
        return Arrays.stream(distribution).mapToLong(LongAdder::sum).toArray();
    }
}
