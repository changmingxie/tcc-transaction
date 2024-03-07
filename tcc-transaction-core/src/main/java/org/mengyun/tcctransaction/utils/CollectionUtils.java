package org.mengyun.tcctransaction.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Created by changming.xie on 2/21/16.
 */
public final class CollectionUtils {

    private CollectionUtils() {

    }

    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }


    public static List merge(List firstList, List secondList) {

        List mergedList = new ArrayList<>();

        if (firstList != null) {
            mergedList.addAll(firstList);
        }

        if (secondList != null) {
            mergedList.addAll(secondList);
        }
        return mergedList;
    }

    /**
     * Workaround for problem: https://bugs.openjdk.java.net/browse/JDK-8161372
     */
    public static <K, V> V fixedConcurrentComputeIfAbsent(ConcurrentMap<K, V> concurrentMap, K key,
                                                          Function<? super K, ? extends V> mappingFunction) {
        V v, newValue;
        return ((v = concurrentMap.get(key)) == null && (newValue = mappingFunction.apply(key)) != null &&
                (v = concurrentMap.putIfAbsent(key, newValue)) == null) ? newValue : v;
    }
}