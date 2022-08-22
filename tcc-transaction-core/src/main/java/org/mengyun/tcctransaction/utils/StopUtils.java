package org.mengyun.tcctransaction.utils;

/**
 * @author Nervose.Wu
 * @date 2022/7/7 16:39
 */
public class StopUtils {

    private StopUtils() {
    }

    /**
     * call exit in a new thread to avoid potential dead lock
     */
    public static void stop() {
        new Thread(() -> System.exit(1)).start();
    }
}
