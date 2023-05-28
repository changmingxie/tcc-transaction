package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.constants.RemotingServiceCode;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.remoting.RemotingClient;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RemotingTransactionStorage extends AbstractTransactionStorage {

    static final Logger logger = LoggerFactory.getLogger(RocksDbTransactionStorage.class.getSimpleName());

    private RemotingClient remotingClient;

    public RemotingTransactionStorage(TransactionStoreSerializer serializer, StoreConfig storeConfig) {
        super(serializer, storeConfig);
    }

    @Override
    public int create(TransactionStore transactionStore) {
        if (transactionStore.getContent().length > this.storeConfig.getMaxTransactionSize()) {
            throw new TransactionIOException(String.format("cur transaction size(%dB) is bigger than maxTransactionSize(%dB), consider to reduce parameter size or adjust maxTransactionSize", transactionStore.getContent().length, this.storeConfig.getMaxTransactionSize()));
        }
        int result = doCreate(transactionStore);
        if (result > 0) {
            return result;
        } else {
            //no need to check again since the actual transactionStorage has already do that
            throw new TransactionIOException(transactionStore.simpleDetail());
        }
    }

    @Override
    public int update(TransactionStore transactionStore) {
        int result = doUpdate(transactionStore);
        if (result > 0) {
            return result;
        } else {
            //no need to check again since the actual transactionStorage has already do that
            throw new TransactionOptimisticLockException(transactionStore.simpleDetail());
        }
    }

    @Override
    protected int doCreate(TransactionStore transactionStore) {
        return doWrite(RemotingServiceCode.CREATE, transactionStore);
    }

    @Override
    protected int doUpdate(TransactionStore transactionStore) {
        return doWrite(RemotingServiceCode.UPDATE, transactionStore);
    }

    @Override
    protected int doDelete(TransactionStore transactionStore) {
        return doWrite(RemotingServiceCode.DELETE, transactionStore);
    }

    @Override
    protected int doMarkDeleted(TransactionStore transactionStore) {
        throw new UnsupportedOperationException("doMarkDeleted not support at remoting");
    }

    @Override
    protected int doRestore(TransactionStore transactionStore) {
        throw new UnsupportedOperationException("doRestore not support at remoting");
    }

    @Override
    protected int doCompletelyDelete(TransactionStore transactionStore) {
        throw new UnsupportedOperationException("doCompletelyDelete not support at remoting");
    }

    @Override
    protected TransactionStore doFindOne(String domain, Xid xid, boolean isMarkDeleted) {
        if (isMarkDeleted) {
            throw new UnsupportedOperationException("doFindOne for markDeleted not support at remoting");
        }
        return doRead(RemotingServiceCode.FIND, domain, xid);
    }

    @Override
    public void close() {
    }

    private int doWrite(int serviceCode, TransactionStore transactionStore) {
        RemotingCommand requestCommand = RemotingCommand.createCommand(RemotingCommandCode.SERVICE_REQ, null);
        requestCommand.setServiceCode(serviceCode);
        requestCommand.setBody(serializer.serialize(transactionStore));
        RemotingCommand responseCommand = null;
        responseCommand = remotingClient.invokeSync(requestCommand, this.storeConfig.getRequestTimeoutMillis());
        if (responseCommand.getCode() == RemotingCommandCode.SERVICE_RESP) {
            return Byte.valueOf(responseCommand.getBody()[0]).intValue();
        } else {
            throw new SystemException("invokeSync failed, the response command code: " + responseCommand.getCode() + ". detail message: " + responseCommand.getRemark());
        }
    }

    private TransactionStore doRead(int serviceCode, String domain, Xid xid) {
        RemotingCommand requestCommand = RemotingCommand.createCommand(RemotingCommandCode.SERVICE_REQ, null);
        requestCommand.setServiceCode(serviceCode);
        byte[] domainBytes = domain.getBytes(StandardCharsets.UTF_8);
        byte[] xidBytes = xid.getXid().getBytes(StandardCharsets.UTF_8);
        byte domainByteLength = (byte) domainBytes.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1 + domainBytes.length + xidBytes.length);
        byteBuffer.put(domainByteLength);
        byteBuffer.put(domainBytes);
        byteBuffer.put(xidBytes);
        requestCommand.setBody(byteBuffer.array());
        RemotingCommand responseCommand = remotingClient.invokeSync(requestCommand, this.storeConfig.getRequestTimeoutMillis());
        if (responseCommand.getCode() == RemotingCommandCode.SERVICE_RESP) {
            return serializer.deserialize(responseCommand.getBody());
        } else {
            throw new SystemException("invokeSync failed, the response command code:" + responseCommand.getCode() + "." + responseCommand.getRemark());
        }
    }

    public RemotingClient getRemotingClient() {
        return remotingClient;
    }

    public void setRemotingClient(RemotingClient remotingClient) {
        this.remotingClient = remotingClient;
    }

    @Override
    public boolean supportStorageRecoverable() {
        return false;
    }

    public void setSerializer(TransactionStoreSerializer serializer) {
        this.serializer = serializer;
    }
}
