package org.mengyun.tcctransaction.utils;

import java.nio.ByteBuffer;

/**
 * Created by changming.xie on 2/14/16.
 */
public class ByteUtils {

    public static byte[] longToBytes(long l) {
        return ByteBuffer.allocate(8).putLong(l).array();
    }

    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }
}
