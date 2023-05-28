package org.mengyun.tcctransaction.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
}
