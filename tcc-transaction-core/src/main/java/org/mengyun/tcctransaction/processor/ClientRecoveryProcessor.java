package org.mengyun.tcctransaction.processor;

import io.netty.channel.ChannelHandlerContext;
import org.mengyun.tcctransaction.constants.RemotingServiceCode;
import org.mengyun.tcctransaction.recovery.RecoveryExecutor;
import org.mengyun.tcctransaction.remoting.RequestProcessor;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.TransactionStore;

public class ClientRecoveryProcessor implements RequestProcessor<ChannelHandlerContext> {

    private RecoveryExecutor recoveryExecutor;
    private TransactionStoreSerializer serializer;

    public ClientRecoveryProcessor(TransactionStoreSerializer serializer, RecoveryExecutor recoveryExecutor) {
        this.serializer = serializer;
        this.recoveryExecutor = recoveryExecutor;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {

        TransactionStore transactionStore = serializer.deserialize(request.getBody());

        switch (request.getServiceCode()) {
            case RemotingServiceCode.RECOVER_COMMIT:
                recoveryExecutor.commit(transactionStore);
                break;
            case RemotingServiceCode.RECOVER_ROLLBACK:
                recoveryExecutor.rollback(transactionStore);
                break;
        }


        RemotingCommand remotingCommand = RemotingCommand.createServiceResponseCommand(null);
        return remotingCommand;
    }
}
