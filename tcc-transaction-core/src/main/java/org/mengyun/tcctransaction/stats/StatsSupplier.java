package org.mengyun.tcctransaction.stats;

import java.util.List;

/**
 * @author Nervose.Wu
 * @date 2024/1/22 19:17
 */
public interface StatsSupplier {
    List<StatsDto> getStatsDtoList();

}
