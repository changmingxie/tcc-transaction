package org.mengyun.tcctransaction.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.Transaction;

import java.io.IOException;

/**
 * Created by changming.xie on 04/12/19.
 */
//@Deprecated
public class JacksonTransactionSerializer implements TransactionSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
//        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL,"@class");
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        objectMapper.activateDefaultTyping();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    }

    @Override
    public byte[] serialize(Transaction transaction) {
        try {
            return objectMapper.writeValueAsBytes(transaction);
        } catch (JsonProcessingException e) {
            throw new SystemException(String.format("serialize object failed. object:%s", transaction), e);
        }
    }

    @Override
    public Transaction deserialize(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, Transaction.class);
        } catch (IOException e) {
            throw new SystemException(String.format("deserialize object failed."), e);
        }
    }

    @Override
    public Transaction clone(Transaction object) {
        return deserialize(serialize(object));
    }

}
