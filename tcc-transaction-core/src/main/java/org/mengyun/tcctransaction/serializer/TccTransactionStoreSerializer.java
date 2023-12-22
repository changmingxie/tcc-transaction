package org.mengyun.tcctransaction.serializer;

import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.xid.TransactionXid;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Nervose.Wu
 * @date 2022/6/22 14:11
 */
public class TccTransactionStoreSerializer implements TransactionStoreSerializer {

    private static final int REQUEST_ID_EXIST_FLAG_POS = 1 << 0;

    private static final int ID_EXIST_FLAG_POS = 1 << 1;

    @Override
    public byte[] serialize(TransactionStore transactionStore) {
        if (transactionStore == null) {
            return new byte[0];
        }

        byte existFlag = 0;
        boolean existRequestId = transactionStore.getRequestId() != null;
        if (existRequestId) {
            existFlag |= REQUEST_ID_EXIST_FLAG_POS;
        }
        boolean existId = transactionStore.getId() != null;
        if (existId) {
            existFlag |= ID_EXIST_FLAG_POS;
        }

        Xid xid = transactionStore.getXid();
        Xid roodXid = transactionStore.getRootXid();
        int xidLength = xid.getXid().getBytes().length;
        int rootXidLength = roodXid.getXid().getBytes().length;

        //domain
        byte[] domainBytes = null;
        int domainLength = 0;
        if (transactionStore.getDomain() != null && !transactionStore.getDomain().isEmpty()) {
            domainBytes = transactionStore.getDomain().getBytes(StandardCharsets.UTF_8);
            domainLength = domainBytes.length;
        }
        //rootDomain
        byte[] rootDomainBytes = null;
        int rootDomainLength = 0;
        if (transactionStore.getRootDomain() != null && !transactionStore.getRootDomain().isEmpty()) {
            rootDomainBytes = transactionStore.getRootDomain().getBytes(StandardCharsets.UTF_8);
            rootDomainLength = rootDomainBytes.length;
        }
        //content
        int contentLength = 0;
        if (transactionStore.getContent() != null && transactionStore.getContent().length > 0) {
            contentLength = transactionStore.getContent().length;
        }

        int totalLen = calTotalLength(domainLength, rootDomainLength, xidLength, rootXidLength, contentLength, existFlag);

        ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
        byteBuffer.put(existFlag);
        //domain
        byteBuffer.putInt(domainLength);
        if (domainLength > 0) {
            byteBuffer.put(domainBytes);
        }
        //rootDomain
        byteBuffer.putInt(rootDomainLength);
        if (rootDomainLength > 0) {
            byteBuffer.put(rootDomainBytes);
        }
        // TransactionXid xid
        byteBuffer.putInt(xidLength);
        byteBuffer.put(xid.getXid().getBytes());
        // TransactionXid rootXid
        byteBuffer.putInt(rootXidLength);
        byteBuffer.put(roodXid.getXid().getBytes());
        //content
        byteBuffer.putInt(contentLength);
        if (contentLength > 0) {
            byteBuffer.put(transactionStore.getContent());
        }
        byteBuffer.putLong(transactionStore.getCreateTime().getTime());
        byteBuffer.putLong(transactionStore.getLastUpdateTime().getTime());
        byteBuffer.putLong(transactionStore.getVersion());
        byteBuffer.putInt(transactionStore.getRetriedCount());
        byteBuffer.putInt(transactionStore.getStatusId());
        byteBuffer.putInt(transactionStore.getTransactionTypeId());
        if (existRequestId) {
            byteBuffer.putInt(transactionStore.getRequestId());
        }
        if (existId) {
            byteBuffer.putLong(transactionStore.getId());
        }
        return byteBuffer.array();
    }

    @Override
    public TransactionStore deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        TransactionStore transactionStore = new TransactionStore();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byte existFlag = byteBuffer.get();
        //domain
        int domainLength = byteBuffer.getInt();
        if (domainLength > 0) {
            byte[] domainContent = new byte[domainLength];
            byteBuffer.get(domainContent);
            transactionStore.setDomain(new String(domainContent, StandardCharsets.UTF_8));
        }
        //rootDomain
        int rootDomainLength = byteBuffer.getInt();
        if (rootDomainLength > 0) {
            byte[] rootDomainContent = new byte[rootDomainLength];
            byteBuffer.get(rootDomainContent);
            transactionStore.setRootDomain(new String(rootDomainContent, StandardCharsets.UTF_8));
        }
        // TransactionXid xid
        int xidLength = byteBuffer.getInt();
        byte[] xidContent = new byte[xidLength];
        byteBuffer.get(xidContent);
        TransactionXid xid = new TransactionXid(new String(xidContent, StandardCharsets.UTF_8));
        transactionStore.setXid(xid);
        // TransactionXid rootXid
        int rootXidLength = byteBuffer.getInt();
        byte[] rootXidContent = new byte[rootXidLength];
        byteBuffer.get(rootXidContent);
        TransactionXid rootXid = new TransactionXid(new String(rootXidContent, StandardCharsets.UTF_8));
        transactionStore.setRootXid(rootXid);
        //byte[] content
        int contentLength = byteBuffer.getInt();
        if (contentLength > 0) {
            byte[] content = new byte[contentLength];
            byteBuffer.get(content);
            transactionStore.setContent(content);
        }
        transactionStore.setCreateTime(new Date(byteBuffer.getLong()));
        transactionStore.setLastUpdateTime(new Date(byteBuffer.getLong()));
        transactionStore.setVersion(byteBuffer.getLong());
        transactionStore.setRetriedCount(byteBuffer.getInt());
        transactionStore.setStatusId(byteBuffer.getInt());
        transactionStore.setTransactionTypeId(byteBuffer.getInt());
        if ((existFlag & REQUEST_ID_EXIST_FLAG_POS) != 0) {
            transactionStore.setRequestId(byteBuffer.getInt());
        }
        if ((existFlag & ID_EXIST_FLAG_POS) != 0) {
            transactionStore.setId(byteBuffer.getLong());
        }
        return transactionStore;
    }

    @Override
    public TransactionStore clone(TransactionStore original) {
        if (original == null) {
            return null;
        }
        TransactionStore cloned = new TransactionStore();
        cloned.setVersion(original.getVersion());
        cloned.setXid(original.getXid());
        cloned.setStatusId(original.getStatusId());
        cloned.setTransactionTypeId(original.getTransactionTypeId());
        cloned.setRetriedCount(original.getRetriedCount());
        cloned.setCreateTime(original.getCreateTime());
        cloned.setLastUpdateTime(original.getLastUpdateTime());
        cloned.setContent(original.getContent().clone());
        cloned.setDomain(original.getDomain());
        cloned.setRootXid(original.getRootXid());
        cloned.setRootDomain(original.getRootDomain());
        cloned.setRequestId(original.getRequestId());
        cloned.setId(original.getId());
        return cloned;
    }

    private int calTotalLength(int domainLength, int rootDomainLength, int xidLength, int rootXidLength, int contentLength, byte existFlag) {
        // existFlag to distinguish whether optional params existed
        int totalLength = 1
                //String domain
                + 4 + domainLength
                //String rootDomainLength
                + 4 + rootDomainLength
                //TransactionXid xid
                + 4 + xidLength
                //TransactionXid rootXid
                + 4 + rootXidLength
                //byte[] content
                + 4 + contentLength
                //Date createTime
                + 8
                //Date lastUpdateTime
                + 8
                //long version
                + 8
                //int retriedCount
                + 4
                //int statusId
                + 4
                //int transactionTypeId
                + 4;
        if ((existFlag & REQUEST_ID_EXIST_FLAG_POS) != 0) {
            totalLength = totalLength
                    //Integer requestId
                    + 4;
        }
        if ((existFlag & ID_EXIST_FLAG_POS) != 0) {
            totalLength = totalLength
                    //Long id
                    + 8;
        }
        return totalLength;
    }
}
