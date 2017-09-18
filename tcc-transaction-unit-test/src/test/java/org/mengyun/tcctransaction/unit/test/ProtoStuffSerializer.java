package org.mengyun.tcctransaction.unit.test;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;

/**
 * Created by changming.xie on 9/17/17.
 */
public class ProtoStuffSerializer implements ObjectSerializer<Transaction> {

    @Override
    public byte[] serialize(Transaction t) {


        return ProtobufIOUtil.toByteArray(t, RuntimeSchema.createFrom(Transaction.class),
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    @Override
    public Transaction deserialize(byte[] data) {
        RuntimeSchema<Transaction> runtimeSchema = RuntimeSchema.createFrom(Transaction.class);
        Transaction t = runtimeSchema.newMessage();
        ProtobufIOUtil.mergeFrom(data, t, runtimeSchema);
        return t;
    }

    @Override
    public Transaction clone(Transaction object) {
        throw new UnsupportedOperationException();
    }
}

