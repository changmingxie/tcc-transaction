package org.mengyun.tcctransaction.utils;

import java.nio.ByteBuffer;

/**
 * Created by changming.xie on 2/14/16.
 */
public class ByteUtils {

    public static final byte[] TRUE;
    public static final byte[] FALSE;

    static {
        TRUE = bool2bytes(true);
        FALSE = bool2bytes(false);
    }


    public static byte[] longToBytes(long num) {
        return String.valueOf(num).getBytes();
    }

    public static long bytesToLong(byte[] bytes) {
        return Long.valueOf(new String(bytes));
    }

    public static byte[] intToBytes(int num) {
        return String.valueOf(num).getBytes();
    }

    public static int bytesToInt(byte[] bytes) {
        return Integer.valueOf(new String(bytes));
    }

    /**
     * @param i boolean
     * @return byte[]
     */
    public static byte[] bool2bytes(boolean i) {
        ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.putInt(i ? 1 : 0);
        return allocate.array();
    }

    public static boolean bytes2bool(byte[] bytes) {
        ByteBuffer allocate = ByteBuffer.wrap(bytes);
        return allocate.getInt() != 0;
    }

}
