package org.mengyun.tcctransaction.stats;

import java.util.Arrays;
import java.util.List;

/**
 * @author Nervose.Wu
 * @date 2024/1/29 10:51
 */

public enum StatsCategory {
    TCC_SERVER_SUCCESS_RPC_REQUEST_NUMS(StatsType.COUNTER, Arrays.asList("ServiceCode"), 16, null),
    TCC_SERVER_FAIL_RPC_REQUEST_NUMS(StatsType.COUNTER, Arrays.asList("ServiceCode"), 16, null),
    TCC_SERVER_RPC_REQUEST_COSTS_DISTRIBUTION(StatsType.HISTOGRAM, Arrays.asList("ServiceCode"), 16, new long[]{5, 10, 20, 40, 80, 160, 320, 640, 1280, 2560, 5120, Long.MAX_VALUE}),
    ;

    private final StatsType statsType;

    private final List<String> dimensions;

    private final int initialCapacity;

    /**
     * only used by HISTOGRAM type and the last element should be Long.MAX_VALUE
     */
    private final long[] buckets;

    StatsCategory(StatsType statsType, List<String> dimensions, int initialCapacity, long[] buckets) {
        this.statsType = statsType;
        this.dimensions = dimensions;
        this.initialCapacity = initialCapacity;
        this.buckets = buckets;
    }

    public StatsType getStatsType() {
        return statsType;
    }

    public List<String> getDimensions() {
        return dimensions;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public long[] getBuckets() {
        return buckets;
    }
}
