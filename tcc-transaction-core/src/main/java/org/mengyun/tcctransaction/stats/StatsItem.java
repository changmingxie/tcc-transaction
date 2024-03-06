package org.mengyun.tcctransaction.stats;

/**
 * @author Nervose.Wu
 * @date 2024/1/29 11:15
 */
public abstract class StatsItem {

    protected final String statsName;
    protected final String statsKey;

    public StatsItem(String statsName, String statsKey) {
        this.statsName = statsName;
        this.statsKey = statsKey;
    }

    public String getStatsName() {
        return statsName;
    }

    public String getStatsKey() {
        return statsKey;
    }
}
