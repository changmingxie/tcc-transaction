package org.mengyun.tcctransaction.dashboard.service.condition;

import org.mengyun.tcctransaction.dashboard.enums.DataFetchType;
import org.springframework.util.StringUtils;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 11:56
 **/
public class TccServerStorageCondition extends BaseStorageCondition {
    @Override
    boolean match(String dataFetchTypeVaule, String storageTypeVaule) {

        if (dataFetchTypeVaule.toUpperCase().equals(DataFetchType.TCCSERVER.name())) {
            if (!StringUtils.isEmpty(storageTypeVaule)) {
                throw new RuntimeException("storageType must null, when dataFetchType is " + DataFetchType.TCCSERVER.name());
            }
            return true;
        }

        return false;
    }


}
