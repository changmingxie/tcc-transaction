package org.mengyun.tcctransaction.serializer;

import org.mengyun.tcctransaction.api.ParticipantStatus;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.xid.TransactionXid;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 *
 * @author Nervose.Wu
 * @date 2022/6/17 17:16
 */

public class TransactionContextSerializer implements ObjectSerializer<TransactionContext> {

    private static byte[] mapSerialize(Map<String, String> map) {
        // keySize+key+valSize+val
        if (null == map || map.isEmpty()) {
            return null;
        }

        int totalLength = 0;
        int kvLength;
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                kvLength =
                        // keySize + Key
                        2 + entry.getKey().getBytes(StandardCharsets.UTF_8).length
                                // valSize + val
                                + 4 + entry.getValue().getBytes(StandardCharsets.UTF_8).length;
                totalLength += kvLength;
            }
        }

        ByteBuffer content = ByteBuffer.allocate(totalLength);
        byte[] key;
        byte[] val;
        it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                key = entry.getKey().getBytes(StandardCharsets.UTF_8);
                val = entry.getValue().getBytes(StandardCharsets.UTF_8);

                content.putShort((short) key.length);
                content.put(key);

                content.putInt(val.length);
                content.put(val);
            }
        }

        return content.array();
    }

    private static Map<String, String> mapDeserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        Map<String, String> map = new ConcurrentHashMap<>();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        short keySize;
        byte[] keyContent;
        int valSize;
        byte[] valContent;
        while (byteBuffer.hasRemaining()) {
            keySize = byteBuffer.getShort();
            keyContent = new byte[keySize];
            byteBuffer.get(keyContent);

            valSize = byteBuffer.getInt();
            valContent = new byte[valSize];
            byteBuffer.get(valContent);

            map.put(new String(keyContent, StandardCharsets.UTF_8), new String(valContent, StandardCharsets.UTF_8));
        }
        return map;
    }

    @Override
    public byte[] serialize(TransactionContext transactionContext) {
        if (transactionContext == null) {
            return new byte[0];
        }
        Xid xid = transactionContext.getXid();
        Xid rootXid = transactionContext.getRootXid();
        String rootDomain = transactionContext.getRootDomain();
        int xidLength = xid.getXid().getBytes().length;
        int rootXidLength = rootXid.getXid().getBytes().length;
        int rootDomainLength = transactionContext.getRootDomain().getBytes().length;

        byte[] attachmentsBytes = null;
        int attachmentsLength = 0;
        if (transactionContext.getAttachments() != null && !transactionContext.getAttachments().isEmpty()) {
            attachmentsBytes = mapSerialize(transactionContext.getAttachments());
            attachmentsLength = attachmentsBytes.length;
        }

        int totalLen = calTotalLength(xidLength, rootXidLength, rootDomainLength, attachmentsLength);

        ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
        // TransactionXid xid
        byteBuffer.putInt(xidLength);
        byteBuffer.put(xid.getXid().getBytes());
        // TransactionXid rootXid
        byteBuffer.putInt(rootXidLength);
        byteBuffer.put(rootXid.getXid().getBytes());
        // String rootDomain
        byteBuffer.putInt(rootDomainLength);
        byteBuffer.put(rootDomain.getBytes());
        // int status
        byteBuffer.putInt(transactionContext.getStatus().getId());
        // int participantStatus
        byteBuffer.putInt(transactionContext.getParticipantStatus().getId());
        // Map<String, String> attachments
        if (attachmentsBytes != null) {
            byteBuffer.putInt(attachmentsLength);
            byteBuffer.put(attachmentsBytes);
        } else {
            byteBuffer.putInt(0);
        }

        return byteBuffer.array();
    }

    @Override
    public TransactionContext deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        TransactionContext transactionContext = new TransactionContext();

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        // TransactionXid xid
        int xidLength = byteBuffer.getInt();
        byte[] xidContent = new byte[xidLength];
        byteBuffer.get(xidContent);
        TransactionXid xid = new TransactionXid(new String(xidContent, StandardCharsets.UTF_8));
        transactionContext.setXid(xid);
        // TransactionXid rootXid
        int rootXidLength = byteBuffer.getInt();
        byte[] rootXidContent = new byte[rootXidLength];
        byteBuffer.get(rootXidContent);
        TransactionXid rootXid = new TransactionXid(new String(rootXidContent, StandardCharsets.UTF_8));
        transactionContext.setRootXid(rootXid);

        // rootDomain
        int rootDomainLength = byteBuffer.getInt();
        byte[] rootDomainContent = new byte[rootDomainLength];
        byteBuffer.get(rootDomainContent);
        transactionContext.setRootDomain(new String(rootDomainContent, StandardCharsets.UTF_8));

        // int status
        transactionContext.setStatus(TransactionStatus.valueOf(byteBuffer.getInt()));
        // int participantStatus
        transactionContext.setParticipantStatus(ParticipantStatus.valueOf(byteBuffer.getInt()));
        // Map<String, String> attachments
        int attachmentsLength = byteBuffer.getInt();
        if (attachmentsLength > 0) {
            byte[] attachmentsBytes = new byte[attachmentsLength];
            transactionContext.setAttachments(mapDeserialize(attachmentsBytes));
        }
        transactionContext.setAttachments(new ConcurrentHashMap<>());

        return transactionContext;
    }

    @Override
    public TransactionContext clone(TransactionContext original) {
        if (original == null) {
            return null;
        }
        TransactionContext cloned = new TransactionContext();
        cloned.setXid(original.getXid());
        cloned.setAttachments(original.getAttachments());
        cloned.setStatus(original.getStatus());
        cloned.setParticipantStatus(original.getParticipantStatus());
        cloned.setRootXid(original.getRootXid());
        return cloned;
    }

    private int calTotalLength(int xidLength, int rootXidLength, int rootDomainLength, int attachmentsLength) {
        // TransactionXid xid
        return 4 + xidLength
                // TransactionXid rootXid
                + 4 + rootXidLength
                // TransactionXid rootDomain
                + 4 + rootDomainLength
                // int status
                + 4
                // int participantStatus
                + 4
                // Map<String, String> attachments
                + 4 + attachmentsLength;
    }
}
