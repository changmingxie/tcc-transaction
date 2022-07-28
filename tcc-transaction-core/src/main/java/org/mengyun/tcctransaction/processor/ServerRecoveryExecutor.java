package org.mengyun.tcctransaction.processor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.mengyun.tcctransaction.constants.RemotingServiceCode;
import org.mengyun.tcctransaction.recovery.RecoveryExecutor;
import org.mengyun.tcctransaction.recovery.RecoveryScheduler;
import org.mengyun.tcctransaction.remoting.RemotingServer;
import org.mengyun.tcctransaction.remoting.netty.ChannelGroupMap;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRecoveryExecutor implements RecoveryExecutor {

    static final Logger logger = LoggerFactory.getLogger(ServerRecoveryExecutor.class.getSimpleName());
    // the timeout for tcc-server reqeust tcc-client
    private static final long REQUEST_TIMEOUT_MILLIS = 2000L;
    private TransactionStoreSerializer serializer;
    private RecoveryScheduler scheduler;
    private RemotingServer remotingServer;

    public ServerRecoveryExecutor(RecoveryScheduler scheduler, TransactionStoreSerializer transactionStoreSerializer, RemotingServer remotingServer) {
        this.serializer = transactionStoreSerializer;
        this.scheduler = scheduler;
        this.remotingServer = remotingServer;
    }

    @Override
    public void rollback(TransactionStore transactionStore) {
        doRecover(RemotingServiceCode.RECOVER_ROLLBACK, transactionStore);
    }

    @Override
    public void commit(TransactionStore transactionStore) {
        doRecover(RemotingServiceCode.RECOVER_COMMIT, transactionStore);
    }

    @Override
    public byte[] transactionVisualize(String domain, byte[] content) {

        RemotingCommand requestCommand = RemotingCommand.createCommand(RemotingCommandCode.SERVICE_REQ, null);
        requestCommand.setServiceCode(RemotingServiceCode.DESERIALIZE_TRANSACTION);
        requestCommand.setBody(content);

        RemotingCommand remotingCommand = this.remotingServer.invokeSync(domain, requestCommand, REQUEST_TIMEOUT_MILLIS);

        return remotingCommand.getBody();

    }

    private void doRecover(int serviceCode, TransactionStore transactionStore) {
        RemotingCommand remotingCommand = RemotingCommand.createCommand(RemotingCommandCode.SERVICE_REQ, null);
        remotingCommand.setServiceCode(serviceCode);
        remotingCommand.setBody(serializer.serialize(transactionStore));

        Channel channel = FactoryBuilder.factoryOf(ChannelGroupMap.class).getInstance().getChannel(transactionStore.getDomain());

        if (channel == null) {
            logger.debug(String.format("no available client channels for domain<%s> to recovery", transactionStore.getDomain()));
            scheduler.unregisterSchedule(transactionStore.getDomain());
            return;
        }

        try {
            channel.writeAndFlush(remotingCommand).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        logger.warn("send recovery command with service code " + serviceCode + " to channel <" + channel.remoteAddress() + "> failed.");
                    }
                }
            });
        } catch (Exception e) {
            logger.warn("cannot recover", e);
        }
    }
}
