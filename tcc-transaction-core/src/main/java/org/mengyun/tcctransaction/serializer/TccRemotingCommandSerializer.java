package org.mengyun.tcctransaction.serializer;

import org.mengyun.tcctransaction.remoting.exception.RemotingCommandException;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class TccRemotingCommandSerializer implements RemotingCommandSerializer {

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    @Override
    public byte[] serialize(RemotingCommand remotingCommand) {

        // String remark
        byte[] remarkBytes = null;
        int remarkLength = 0;
        if (remotingCommand.getRemark() != null && remotingCommand.getRemark().length() > 0) {
            remarkBytes = remotingCommand.getRemark().getBytes(CHARSET_UTF8);
            remarkLength = remarkBytes.length;
        }

        int bodyLength = 0;
        if (remotingCommand.getBody() != null && remotingCommand.getBody().length > 0) {
            bodyLength = remotingCommand.getBody().length;
        }

        int totalLength = calTotalLength(remarkLength, bodyLength);

        ByteBuffer byteBuffer = ByteBuffer.allocate(totalLength);
        //byte RemotingCommandCode
        byteBuffer.put(remotingCommand.getCode().value());
        //int requestId
        byteBuffer.putInt(remotingCommand.getRequestId());
        //int serviceCode
        byteBuffer.putInt(remotingCommand.getServiceCode());

        //byte[] remarkBytes
        if (remarkBytes != null) {
            //int remarkLength
            byteBuffer.putInt(remarkLength);
            byteBuffer.put(remarkBytes);
        } else {
            byteBuffer.putInt(0);
        }

        if (remotingCommand.getBody() != null) {
            //int bodyLength
            byteBuffer.putInt(bodyLength);
            //byte[] bodyBytes
            byteBuffer.put(remotingCommand.getBody());
        } else {
            byteBuffer.putInt(0);
        }

        return byteBuffer.array();
    }

    @Override
    public RemotingCommand deserialize(byte[] bytes) {
        RemotingCommand remotingCommand = new RemotingCommand();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        //byte RemotingCommandCode
        remotingCommand.setCode(RemotingCommandCode.valueOf(byteBuffer.get()));
        //int requestId
        remotingCommand.setRequestId(byteBuffer.getInt());
        //int serviceCode
        remotingCommand.setServiceCode(byteBuffer.getInt());
        //int remarkLength
        int remarkLength = byteBuffer.getInt();
        if (remarkLength > 0) {
            if (remarkLength > bytes.length) {
                throw new RemotingCommandException("Tcc Remoting protocol decoding failed, remark length: " + remarkLength + ", but total length: " + bytes.length);
            }
            byte[] remarkContent = new byte[remarkLength];
            byteBuffer.get(remarkContent);
            remotingCommand.setRemark(new String(remarkContent, CHARSET_UTF8));
        }

        //int bodyLength
        int bodyLength = byteBuffer.getInt();
        if (bodyLength > 0) {
            if (bodyLength > bytes.length) {
                throw new RemotingCommandException("Tcc Remoting protocol decoding failed, body length: " + bodyLength + ", but total length: " + bytes.length);
            }
            byte[] body = new byte[bodyLength];
            byteBuffer.get(body);
            remotingCommand.setBody(body);
        }

        return remotingCommand;
    }

    @Override
    public RemotingCommand clone(RemotingCommand original) {
        if (original == null) {
            return null;
        }

        RemotingCommand cloned = new RemotingCommand();
        cloned.setCode(original.getCode());
        cloned.setRequestId(original.getRequestId());
        cloned.setServiceCode(original.getServiceCode());
        cloned.setRemark(original.getRemark());
        cloned.setBody(original.getBody());
        return cloned;
    }


    private int calTotalLength(int remarkLength, int bodyLength) {
        // byte RemotingCommandCode(~)
        int length = 1
                // requestId
                + 4
                // serviceCode
                + 4
                // String remark
                + 4 + remarkLength
                // HashMap<String, String> extFields
                + 4 + bodyLength;
        return length;
    }
}
