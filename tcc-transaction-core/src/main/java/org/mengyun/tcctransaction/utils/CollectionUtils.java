package org.mengyun.tcctransaction.utils;

import java.util.Collection;

/**
 * Created by changming.xie on 2/21/16.
 */
public final class CollectionUtils {

    private CollectionUtils() {

    }

    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }
}