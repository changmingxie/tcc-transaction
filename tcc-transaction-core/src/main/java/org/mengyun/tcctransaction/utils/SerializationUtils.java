package org.mengyun.tcctransaction.utils;

import java.io.*;

/**
 * Created by changmingxie on 11/21/15.
 */
public class SerializationUtils {

    public static byte[] serialize(Object object) {
        if(object == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

            try {
                ObjectOutputStream ex = new ObjectOutputStream(baos);
                ex.writeObject(object);
                ex.flush();
            } catch (IOException var3) {
                throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), var3);
            }

            return baos.toByteArray();
        }
    }

    public static Object deserialize(byte[] bytes) {
        if(bytes == null) {
            return null;
        } else {
            try {
                ObjectInputStream ex = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return ex.readObject();
            } catch (IOException var2) {
                throw new IllegalArgumentException("Failed to deserialize object", var2);
            } catch (ClassNotFoundException var3) {
                throw new IllegalStateException("Failed to deserialize object type", var3);
            }
        }
    }
}
