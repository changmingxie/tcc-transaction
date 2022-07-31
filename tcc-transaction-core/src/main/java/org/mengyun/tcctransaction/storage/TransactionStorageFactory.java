package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;

public class TransactionStorageFactory {

    public static TransactionStorage create(TransactionStoreSerializer storeSerializer, StoreConfig storeConfig, boolean useRetryable) {

        String storageClassName;

        if (storeConfig.getStorageType().equals(StorageType.CUSTOMIZED)) {
            storageClassName = storeConfig.getTransactionStorageClass();
        } else {
            storageClassName = StorageType.getStorageClassName(storeConfig.getStorageType());
        }

        try {
            Class klass = Class.forName(storageClassName);

            TransactionStorage transactionStorage = (TransactionStorage) klass.getConstructor(TransactionStoreSerializer.class, StoreConfig.class).newInstance(storeSerializer, storeConfig);

            if (useRetryable) {
                return new RetryableTransactionStorage(Math.max(1, storeConfig.getMaxAttempts()), transactionStorage);
            } else {
                return transactionStorage;
            }
        } catch (Exception e) {
            throw new SystemException(String.format("create transaction storage failed. the class is :%s", storageClassName), e);
        }
    }
}
