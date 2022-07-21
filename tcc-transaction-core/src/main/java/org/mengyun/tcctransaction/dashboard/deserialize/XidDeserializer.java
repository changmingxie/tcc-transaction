package org.mengyun.tcctransaction.dashboard.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.mengyun.tcctransaction.xid.TransactionXid;

import java.io.IOException;

/**
 * @Author huabao.fang
 * @Date 2022/7/21 19:48
 **/
public class XidDeserializer extends JsonDeserializer {
    @Override
    public Object deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        return new TransactionXid(node.get("xid").asText());
    }
}
