package org.mengyun.tcctransaction.utils;

import java.nio.ByteBuffer;

/**
 * Created by changming.xie on 2/14/16.
 */
public class ByteUtils {

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
