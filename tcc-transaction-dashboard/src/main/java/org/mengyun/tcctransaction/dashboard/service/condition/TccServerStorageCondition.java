package org.mengyun.tcctransaction.dashboard.service.condition;

import org.mengyun.tcctransaction.dashboard.enums.ConnectionMode;
import org.springframework.util.StringUtils;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 11:56
 */
public class TccServerStorageCondition extends BaseStorageCondition {

    @Override
    boolean match(String connectionModeValue, String storageTypeVaule) {
        if (connectionModeValue.equalsIgnoreCase(ConnectionMode.SERVER.name())) {
            if (!StringUtils.isEmpty(storageTypeVaule)) {
                throw new IllegalArgumentException("storageType must null, when connectionMode is " + ConnectionMode.SERVER.name());
            }
            return true;
        }
        return false;
    }
}
