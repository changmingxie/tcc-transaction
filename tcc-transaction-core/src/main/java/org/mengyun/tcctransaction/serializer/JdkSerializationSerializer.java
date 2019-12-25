package org.mengyun.tcctransaction.serializer;

import org.apache.commons.lang3.SerializationUtils;
import org.mengyun.tcctransaction.Transaction;

/**
 * Created by changming.xie on 7/22/16.
 */
public class JdkSerializationSerializer implements ObjectSerializer<Transaction> {

    @Override
    public byte[] serialize(Transaction object) {
        return SerializationUtils.serialize(object);
    }

    @Override
    public Transaction deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            return (Transaction) SerializationUtils.deserialize(bytes);
        }
    }

    @Override
    public Transaction clone(Transaction object) {
        return SerializationUtils.clone(object);
    }
}
