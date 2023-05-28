package org.mengyun.tcctransaction.dashboard.service.condition;

import org.mengyun.tcctransaction.dashboard.enums.ConnectionMode;
import org.mengyun.tcctransaction.storage.StorageType;
import org.springframework.util.StringUtils;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 15:31
 */
public class LocalStorageCondition extends BaseStorageCondition {

    private static final StorageType[] SUPPORT_STORAGETYPES = { StorageType.MEMORY, StorageType.ROCKSDB, StorageType.JDBC, StorageType.REDIS, StorageType.SHARD_REDIS, StorageType.REDIS_CLUSTER };

    @Override
    boolean match(String connectionModeValue, String storageTypeVaule) {
        if (connectionModeValue.equalsIgnoreCase(ConnectionMode.EMBEDDED.name())) {
            if (StringUtils.isEmpty(storageTypeVaule)) {
                throw new IllegalArgumentException("storageType must not null when connectionMode is " + ConnectionMode.EMBEDDED.name());
            }
            boolean isSupportedStorage = false;
            for (StorageType storageType : SUPPORT_STORAGETYPES) {
                if (storageType.name().equalsIgnoreCase(storageTypeVaule)) {
                    isSupportedStorage = true;
                    break;
                }
            }
            if (!isSupportedStorage) {
                throw new UnsupportedOperationException("storageType:" + storageTypeVaule + " not supported, when connectionMode is " + ConnectionMode.EMBEDDED.name());
            }
            return true;
        }
        return false;
    }
}
