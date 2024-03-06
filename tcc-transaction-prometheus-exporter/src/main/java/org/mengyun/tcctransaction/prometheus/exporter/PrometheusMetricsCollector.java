package org.mengyun.tcctransaction.prometheus.exporter;


import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import org.mengyun.tcctransaction.stats.StatsCategory;
import org.mengyun.tcctransaction.stats.StatsDto;
import org.mengyun.tcctransaction.stats.StatsManager;
import org.mengyun.tcctransaction.stats.StatsSupplier;

import java.util.*;

/**
 * @author Nervose.Wu
 * @date 2024/1/22 17:21
 */
public class PrometheusMetricsCollector extends Collector {

    private StatsSupplier statsSupplier;

    private static final String INSTANCE = "Instance";

    public PrometheusMetricsCollector(StatsSupplier statsSupplier) {
        this.statsSupplier = statsSupplier;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<StatsDto> statsDtos = statsSupplier.getStatsDtoList();
        Map<String, Map<String, StatsDto.StatsItemSetDto>> groupedStatsDtos = groupByStatsNameAndInstance(statsDtos);
        List<MetricFamilySamples> samples = new ArrayList<>();
        collectRequestNums(groupedStatsDtos, samples);
        collectRequestCost(groupedStatsDtos, samples);
        return samples;
    }

    private void collectRequestCost(Map<String, Map<String, StatsDto.StatsItemSetDto>> groupedStatsDtos, List<MetricFamilySamples> samples) {
        collectorHistogram(groupedStatsDtos, samples, StatsCategory.TCC_SERVER_RPC_REQUEST_COSTS_DISTRIBUTION);
    }

    private void collectRequestNums(Map<String, Map<String, StatsDto.StatsItemSetDto>> groupedStatsDtos, List<MetricFamilySamples> samples) {
        collectorCounter(groupedStatsDtos, samples, StatsCategory.TCC_SERVER_SUCCESS_RPC_REQUEST_NUMS);
        collectorCounter(groupedStatsDtos, samples, StatsCategory.TCC_SERVER_FAIL_RPC_REQUEST_NUMS);
    }

    private void collectorCounter(Map<String, Map<String, StatsDto.StatsItemSetDto>> groupedStatsDtos, List<MetricFamilySamples> samples, StatsCategory statsCategory) {
        String statsName = statsCategory.name();
        Map<String, StatsDto.StatsItemSetDto> statsItemSetDtoMap = groupedStatsDtos.get(statsName);
        if (!statsItemSetDtoMap.isEmpty()) {
            List<String> labelNames = new ArrayList<>();
            labelNames.add(INSTANCE);
            labelNames.addAll(statsItemSetDtoMap.values().iterator().next().getLabels());
            CounterMetricFamily counterMetricFamily = new CounterMetricFamily(statsName, statsName, labelNames);
            for (Map.Entry<String, StatsDto.StatsItemSetDto> entry : statsItemSetDtoMap.entrySet()) {
                for (Map.Entry<String, Long> valueMapEntry : entry.getValue().getValueMap().entrySet()) {
                    List<String> labelValues = new ArrayList<>();
                    labelValues.add(entry.getKey());
                    labelValues.addAll(Arrays.asList(valueMapEntry.getKey().split(StatsManager.KEY_SPLITER)));
                    counterMetricFamily.addMetric(labelValues, valueMapEntry.getValue());
                }
            }
            if (!counterMetricFamily.samples.isEmpty()) {
                samples.add(counterMetricFamily);
            }
        }
    }

    private void collectorHistogram(Map<String, Map<String, StatsDto.StatsItemSetDto>> groupedStatsDtos, List<MetricFamilySamples> samples, StatsCategory statsCategory) {
        String statsName = statsCategory.name();
        Map<String, StatsDto.StatsItemSetDto> statsItemSetDtoMap = groupedStatsDtos.get(statsName);
        if (!statsItemSetDtoMap.isEmpty()) {
            List<MetricFamilySamples.Sample> curSamples = new ArrayList<>();
            List<String> labelNames = new ArrayList<>();
            labelNames.add(INSTANCE);
            labelNames.addAll(statsItemSetDtoMap.values().iterator().next().getLabels());
            List<String> labelNamesWithLe = new ArrayList<>(labelNames);
            labelNamesWithLe.add("le");
            for (String instanceName : statsItemSetDtoMap.keySet()) {
                StatsDto.StatsItemSetDto statsItemSetDto = statsItemSetDtoMap.get(instanceName);
                for (String labelKey : statsItemSetDto.getValuesMap().keySet()) {
                    long count = 0;
                    List<String> labelValues = new ArrayList<>();
                    labelValues.add(instanceName);
                    labelValues.addAll(Arrays.asList(labelKey.split(StatsManager.KEY_SPLITER)));
                    long[] values = statsItemSetDto.getValuesMap().get(labelKey);
                    long sum = statsItemSetDto.getValueMap().getOrDefault(labelKey, 0L);
                    for (int i = 0; i < statsCategory.getBuckets().length; i++) {
                        List<String> labelValuesWithLe = new ArrayList<>(labelValues);
                        labelValuesWithLe.add(longToGoString(statsCategory.getBuckets()[i]));
                        count += values[i];
                        curSamples.add(new MetricFamilySamples.Sample(statsName + "_bucket", labelNamesWithLe, labelValuesWithLe, count));
                    }
                    curSamples.add(new MetricFamilySamples.Sample(statsName + "_count", labelNames, labelValues, count));
                    curSamples.add(new MetricFamilySamples.Sample(statsName + "_sum", labelNames, labelValues, sum));
                }
            }
            if (!curSamples.isEmpty()) {
                samples.add(new MetricFamilySamples(statsCategory.name(), Type.HISTOGRAM, statsCategory.name(), curSamples));
            }

        }
    }

    private Map<String, Map<String, StatsDto.StatsItemSetDto>> groupByStatsNameAndInstance(List<StatsDto> statsDtos) {
        Map<String, Map<String, StatsDto.StatsItemSetDto>> res = new HashMap<>();
        for (StatsDto statsDto : statsDtos) {
            for (StatsDto.StatsItemSetDto statsItemSetDto : statsDto.getStatsItemSetDtos()) {
                res.computeIfAbsent(statsItemSetDto.getStatsName(), key -> new HashMap<>()).put(statsDto.getInstance(), statsItemSetDto);
            }
        }
        return res;
    }

    private String longToGoString(long value) {
        if (value == Long.MAX_VALUE) {
            return "+Inf";
        }
        if (value == Long.MIN_VALUE) {
            return "-Inf";
        }
        return Long.toString(value);
    }
}
