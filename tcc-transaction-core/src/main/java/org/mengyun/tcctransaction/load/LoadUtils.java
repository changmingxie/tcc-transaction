package org.mengyun.tcctransaction.load;

/**
 * @author Nervose.Wu
 * @date 2022/5/12 19:21
 */
public class LoadUtils {

    private LoadUtils() {
    }

    public static String getServiceName(Class<?> clazz) {
        LoadInfo loadInfo = clazz.getAnnotation(LoadInfo.class);
        if (loadInfo == null) {
            throw new IllegalArgumentException("Failed to get loadInfo");
        }
        return loadInfo.name();
    }
}
