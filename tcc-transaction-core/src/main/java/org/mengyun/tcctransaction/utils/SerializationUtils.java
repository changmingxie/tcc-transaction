package org.mengyun.tcctransaction.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by changmingxie on 11/21/15.
 */
public class SerializationUtils {

    public static byte[] serialize(Object object) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {

            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
