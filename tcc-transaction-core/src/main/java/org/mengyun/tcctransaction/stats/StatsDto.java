package org.mengyun.tcctransaction.stats;

import java.util.List;
import java.util.Map;

/**
 * @author Nervose.Wu
 * @date 2024/1/22 17:40
 */
public class StatsDto {

    private String instance;

    private List<StatsItemSetDto> statsItemSetDtos;

    public StatsDto() {
    }

    public StatsDto(String instance, List<StatsItemSetDto> statsItemSetDtos) {
        this.instance = instance;
        this.statsItemSetDtos = statsItemSetDtos;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public List<StatsItemSetDto> getStatsItemSetDtos() {
        return statsItemSetDtos;
    }

    public void setStatsItemSetDtos(List<StatsItemSetDto> statsItemSetDtos) {
        this.statsItemSetDtos = statsItemSetDtos;
    }

    public static class StatsItemSetDto {
        private String statsName;
        private String statsType;
        private List<String> labels;
        private Map<String, Long> valueMap;
        private Map<String, long[]> valuesMap;

        public StatsItemSetDto() {
        }

        public String getStatsName() {
            return statsName;
        }

        public void setStatsName(String statsName) {
            this.statsName = statsName;
        }

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public String getStatsType() {
            return statsType;
        }

        public void setStatsType(String statsType) {
            this.statsType = statsType;
        }

        public Map<String, Long> getValueMap() {
            return valueMap;
        }

        public void setValueMap(Map<String, Long> valueMap) {
            this.valueMap = valueMap;
        }

        public Map<String, long[]> getValuesMap() {
            return valuesMap;
        }

        public void setValuesMap(Map<String, long[]> valuesMap) {
            this.valuesMap = valuesMap;
        }
    }
}
