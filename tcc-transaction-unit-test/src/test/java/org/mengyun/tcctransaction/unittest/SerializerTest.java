package org.mengyun.tcctransaction.unittest;

import com.google.common.primitives.Longs;
import org.junit.Assert;
import org.junit.Test;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;
import org.mengyun.tcctransaction.serializer.TccRemotingCommandSerializer;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:/tcc-transaction-ut-with-memory-storage.xml" })
public class SerializerTest extends AbstractTestCase {

    @Test
    public void test() {
        long value = 1234l;
        byte[] bytes = Longs.toByteArray(value);
    }

    @Test
    public void given_remoting_command_when_serialize_deserialize_then_original_and_serialized_equals() {
        serialize_and_compare(RemotingCommandCode.SERVICE_RESP, 100, 1, null, null);
        serialize_and_compare(RemotingCommandCode.SERVICE_RESP, 100, 1, "remark1", "my body".getBytes());
    }

    private void serialize_and_compare(RemotingCommandCode remotingCommandCode, int requestId, int serviceCode, String remark, byte[] body) {
        RemotingCommand original = new RemotingCommand();
        original.setCode(remotingCommandCode);
        original.setRequestId(requestId);
        original.setServiceCode(serviceCode);
        original.setRemark(remark);
        original.setBody(body);
        TccRemotingCommandSerializer remotingCommandSerializer = new TccRemotingCommandSerializer();
        byte[] content = remotingCommandSerializer.serialize(original);
        RemotingCommand serialized = remotingCommandSerializer.deserialize(content);
        compare(original, serialized);
    }

    private void compare(RemotingCommand original, RemotingCommand serialized) {
        Assert.assertEquals(original.getCode(), serialized.getCode());
        Assert.assertEquals(original.getRequestId(), serialized.getRequestId());
        Assert.assertEquals(original.getServiceCode(), serialized.getServiceCode());
        Assert.assertEquals(original.getRemark(), serialized.getRemark());
        Assert.assertArrayEquals(original.getBody(), serialized.getBody());
    }
}
