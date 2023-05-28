package org.mengyun.tcctransaction.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import java.io.IOException;

/**
 * @Author huabao.fang
 * @Date 2022/6/29 12:21
 */
public class TccDomainStoreSerializer implements ObjectSerializer<DomainStore> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public byte[] serialize(DomainStore domainStore) {
        try {
            return objectMapper.writeValueAsBytes(domainStore);
        } catch (JsonProcessingException e) {
            throw new SystemException(String.format("serialize object failed. object:%s", domainStore), e);
        }
    }

    @Override
    public DomainStore deserialize(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, DomainStore.class);
        } catch (IOException e) {
            throw new SystemException("deserialize object failed.", e);
        }
    }

    @Override
    public DomainStore clone(DomainStore object) {
        return deserialize(serialize(object));
    }
}
