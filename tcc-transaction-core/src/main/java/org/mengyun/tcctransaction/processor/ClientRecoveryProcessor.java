package org.mengyun.tcctransaction.processor;

import io.netty.channel.ChannelHandlerContext;
import org.mengyun.tcctransaction.constants.RemotingServiceCode;
import org.mengyun.tcctransaction.recovery.RecoveryExecutor;
import org.mengyun.tcctransaction.remoting.RequestProcessor;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;

public class ClientRecoveryProcessor implements RequestProcessor<ChannelHandlerContext> {

    private RecoveryExecutor recoveryExecutor;

    private TransactionStoreSerializer serializer;

    public ClientRecoveryProcessor(TransactionStoreSerializer serializer, RecoveryExecutor recoveryExecutor) {
        this.serializer = serializer;
        this.recoveryExecutor = recoveryExecutor;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
        switch(request.getServiceCode()) {
            case RemotingServiceCode.RECOVER_COMMIT:
                recoveryExecutor.commit(serializer.deserialize(request.getBody()));
                return RemotingCommand.createServiceResponseCommand(null);
            case RemotingServiceCode.RECOVER_ROLLBACK:
                recoveryExecutor.rollback(serializer.deserialize(request.getBody()));
                return RemotingCommand.createServiceResponseCommand(null);
            case RemotingServiceCode.DESERIALIZE_TRANSACTION:
                return processRequestForFindDeserializedTransactionStore(request);
        }
        return RemotingCommand.createServiceResponseCommand(null);
    }

    private RemotingCommand processRequestForFindDeserializedTransactionStore(RemotingCommand request) {
        RemotingCommand responseCommand = RemotingCommand.createServiceResponseCommand(null);
        responseCommand.setBody(recoveryExecutor.transactionVisualize(null, request.getBody()));
        return responseCommand;
    }
}
