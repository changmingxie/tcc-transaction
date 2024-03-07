package org.mengyun.tcctransaction.stats;

import org.mengyun.tcctransaction.utils.CollectionUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 某种类型的指标集合
 * @author Nervose.Wu
 * @date 2024/1/19 14:11
 */
public class StatsItemSet<T> {

    private final StatsCategory statsCategory;

    private final ConcurrentHashMap<String, T> statItemMap;

    public StatsItemSet(StatsCategory statsCategory) {
        this.statsCategory = statsCategory;
        this.statItemMap = new ConcurrentHashMap<>(statsCategory.getInitialCapacity());
    }

    public StatsCategory getStatsCategory() {
        return statsCategory;
    }

    public ConcurrentHashMap<String, T> getStatItemMap() {
        return statItemMap;
    }

    public T getOrCreate(String key, Function<String,T> function){
        return CollectionUtils.fixedConcurrentComputeIfAbsent(statItemMap, key, function);
    }

    //public StatsDto.StatsItemSetDto getData() {
    //    StatsDto.StatsItemSetDto statsItemSetDto = new StatsDto.StatsItemSetDto();
    //    statsItemSetDto.setStatsName(statsName);
    //    statsItemSetDto.setLabels(labels);
    //    statsItemSetDto.setValueMap(statItemMap.values().stream().collect(Collectors.toMap(StatsItem::getStatsKey, StatsItem::getValue)));
    //    return statsItemSetDto;
    //}
}
