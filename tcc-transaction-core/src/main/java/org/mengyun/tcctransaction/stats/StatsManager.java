package org.mengyun.tcctransaction.stats;

import org.mengyun.tcctransaction.constants.RemotingServiceCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nervose.Wu
 * @date 2024/1/19 13:57
 */
public class StatsManager {

    public static final String KEY_SPLITER = "@";

    private final HashMap<StatsCategory, StatsItemSet<? extends StatsItem>> statsTable = new HashMap<>();

    public final String instance;


    public StatsManager(String instance) {
        this.instance = instance;
        for (StatsCategory each : StatsCategory.values()) {
            if (each.getStatsType() == StatsType.COUNTER) {
                statsTable.put(each, new StatsItemSet<CounterStatsItem>(each));
            } else if (each.getStatsType() == StatsType.HISTOGRAM) {
                statsTable.put(each, new StatsItemSet<HistogramStatsItem>(each));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void incSuccessRpcRequestNum(int serviceCode) {
        ((StatsItemSet<CounterStatsItem>) statsTable.get(StatsCategory.TCC_SERVER_SUCCESS_RPC_REQUEST_NUMS))
                .getOrCreate(buildStatesKey(RemotingServiceCode.getDesc(serviceCode)), key -> new CounterStatsItem(StatsCategory.TCC_SERVER_SUCCESS_RPC_REQUEST_NUMS.name(), key)).record(1);
    }

    @SuppressWarnings("unchecked")
    public void incFailRpcRequestNum(int serviceCode) {
        ((StatsItemSet<CounterStatsItem>) statsTable.get(StatsCategory.TCC_SERVER_FAIL_RPC_REQUEST_NUMS))
                .getOrCreate(buildStatesKey(RemotingServiceCode.getDesc(serviceCode)), key -> new CounterStatsItem(StatsCategory.TCC_SERVER_FAIL_RPC_REQUEST_NUMS.name(), key)).record(1);
    }

    @SuppressWarnings("unchecked")
    public void incRpcRequestCost(int serviceCode, long timeInMills) {
        ((StatsItemSet<HistogramStatsItem>) statsTable.get(StatsCategory.TCC_SERVER_RPC_REQUEST_COSTS_DISTRIBUTION))
                .getOrCreate(buildStatesKey(RemotingServiceCode.getDesc(serviceCode)), key -> new HistogramStatsItem(StatsCategory.TCC_SERVER_RPC_REQUEST_COSTS_DISTRIBUTION.name(), key, StatsCategory.TCC_SERVER_RPC_REQUEST_COSTS_DISTRIBUTION.getBuckets())).record(timeInMills);
    }

    public String buildStatesKey(String label) {
        return label;
    }

    public String buildStatesKey(String... labels) {
        StringBuilder sb = new StringBuilder();
        for (String label : labels) {
            sb.append(label);
            sb.append(KEY_SPLITER);
        }
        sb.deleteCharAt(sb.lastIndexOf(KEY_SPLITER));
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public StatsDto getStats() {
        StatsDto statsDto = new StatsDto();
        statsDto.setInstance(instance);

        List<StatsDto.StatsItemSetDto> statsItemSetDtos = new ArrayList<>();
        statsTable.forEach((key, value) -> {
            StatsDto.StatsItemSetDto statsItemSetDto =new StatsDto.StatsItemSetDto();
            statsItemSetDto.setStatsName(key.name());
            statsItemSetDto.setLabels(key.getDimensions());
            statsItemSetDto.setStatsType(key.getStatsType().name());

            if (key.getStatsType() == StatsType.COUNTER) {
                statsItemSetDto.setValueMap(
                        ((Map<String, CounterStatsItem>) value.getStatItemMap()).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, each -> each.getValue().getCount()))
                );
            } else if(key.getStatsType()==StatsType.HISTOGRAM){
                statsItemSetDto.setValueMap(
                        ((Map<String, HistogramStatsItem>) value.getStatItemMap()).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, each -> each.getValue().getSum()))
                );
                statsItemSetDto.setValuesMap(
                        ((Map<String, HistogramStatsItem>) value.getStatItemMap()).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, each -> each.getValue().getDistribution()))
                );
            }
            statsItemSetDtos.add(statsItemSetDto);
        });
        statsDto.setStatsItemSetDtos(statsItemSetDtos);

        return statsDto;
    }
}