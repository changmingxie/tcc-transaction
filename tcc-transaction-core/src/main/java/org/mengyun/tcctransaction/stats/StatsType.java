package org.mengyun.tcctransaction.stats;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Nervose.Wu
 * @date 2024/1/29 10:23
 */
public enum StatsType {
    COUNTER,
    HISTOGRAM,
    ;

    private final static Map<String, StatsType> statsTypeMap;

    static {
        statsTypeMap = Collections.unmodifiableMap(Arrays.stream(StatsType.values()).collect(Collectors.toMap(StatsType::name, Function.identity())));
    }

    public static StatsType findByName(String name) {
        return statsTypeMap.get(name);
    }
}
