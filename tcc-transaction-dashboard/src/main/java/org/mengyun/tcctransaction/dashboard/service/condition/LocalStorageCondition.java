package org.mengyun.tcctransaction.dashboard.service.condition;

import org.mengyun.tcctransaction.dashboard.enums.DataFetchType;
import org.mengyun.tcctransaction.storage.StorageType;
import org.springframework.util.StringUtils;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 15:31
 **/
public class LocalStorageCondition extends BaseStorageCondition {

    private static final StorageType[] SUPPORT_STORAGETYPES = {
            StorageType.MEMORY,
            StorageType.JDBC,
            StorageType.REDIS,
            StorageType.SHARD_REDIS,
            StorageType.REDIS_CLUSTER,
    };

    @Override
    boolean match(String dataFetchTypeVaule, String storageTypeVaule) {

        if (dataFetchTypeVaule.toUpperCase().equals(DataFetchType.LOCAL.name())) {
            if (StringUtils.isEmpty(storageTypeVaule)) {
                throw new RuntimeException("storageType must not null when dataFetchType is " + DataFetchType.LOCAL.name());
            }

            boolean isSupportedStorage = false;
            for (StorageType storageType : SUPPORT_STORAGETYPES) {
                if (storageType.name().equals(storageTypeVaule.toUpperCase())) {
                    isSupportedStorage = true;
                    break;
                }
            }
            if (!isSupportedStorage) {
                throw new RuntimeException("storageType:" + storageTypeVaule + " not supported, when dataFetchType is " + DataFetchType.LOCAL.name());
            }

            return true;
        }

        return false;
    }
}
