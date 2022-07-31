package org.mengyun.tcctransaction.processor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.constants.MixAll;
import org.mengyun.tcctransaction.constants.RemotingServiceCode;
import org.mengyun.tcctransaction.recovery.RecoveryScheduler;
import org.mengyun.tcctransaction.remoting.RequestProcessor;
import org.mengyun.tcctransaction.remoting.netty.ChannelGroupMap;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.*;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.xid.TransactionXid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ServerRequestProcessor implements RequestProcessor<ChannelHandlerContext> {

    static final Logger logger = LoggerFactory.getLogger(ServerRequestProcessor.class.getSimpleName());

    private TransactionStorage transactionStorage;

    private TransactionStoreSerializer serializer;

    private RecoveryScheduler scheduler;

    public ServerRequestProcessor(RecoveryScheduler scheduler, TransactionStoreSerializer serializer, TransactionStorage transactionStorage) {
        this.serializer = serializer;
        this.transactionStorage = transactionStorage;
        this.scheduler = scheduler;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {

        if (request.getServiceCode() == RemotingServiceCode.FIND) {
            return readProcess(ctx, request);
        } else if (request.getServiceCode() == RemotingServiceCode.REGISTER) {
            return register(ctx, request);
        } else {
            return writeProcess(ctx, request);
        }
    }

    private RemotingCommand register(ChannelHandlerContext ctx, RemotingCommand request) {

        //store the domain and the channel map
        String domain = new String(request.getBody());
        Channel channel = ctx.channel();
        channel.attr(AttributeKey.valueOf(MixAll.DOMAIN)).set(domain);

        doRegister(domain, channel);

        RemotingCommand remotingCommand = RemotingCommand.createServiceResponseCommand(null);
        remotingCommand.setBody(new byte[]{(byte) 1});
        return remotingCommand;
    }

    private RemotingCommand readProcess(ChannelHandlerContext ctx, RemotingCommand request) {

        byte[] content = request.getBody();
        byte domainBytesLength = content[0];
        byte[] domainBytes = new byte[domainBytesLength];
        byte[] xidBytes = new byte[content.length - domainBytes.length - 1];

        System.arraycopy(content, 1, domainBytes, 0, domainBytesLength);
        System.arraycopy(content, 1 + domainBytes.length, xidBytes, 0, content.length - domainBytes.length - 1);

        Xid xid = new TransactionXid(new String(xidBytes, StandardCharsets.UTF_8));
        String domain = new String(domainBytes, StandardCharsets.UTF_8);

        TransactionStore transaction = transactionStorage.findByXid(domain, xid);

        RemotingCommand remotingCommand = RemotingCommand.createServiceResponseCommand(null);
        remotingCommand.setBody(serializer.serialize(transaction));
        return remotingCommand;
    }

    private RemotingCommand writeProcess(ChannelHandlerContext ctx, RemotingCommand request) {

        TransactionStore transaction = serializer.deserialize(request.getBody());

        int result = -1;
        switch (request.getServiceCode()) {
            case RemotingServiceCode.CREATE:
                try {
                    result = transactionStorage.create(transaction);
                } catch (TransactionIOException e) {
                    result = 0;
                }
                break;
            case RemotingServiceCode.UPDATE:
                try {
                    result = transactionStorage.update(transaction);
                } catch (TransactionOptimisticLockException e) {
                    result = 0;
                }
                break;
            case RemotingServiceCode.DELETE:
                result = transactionStorage.delete(transaction);
                break;
        }

        if (result > 0) {
            //store the domain and the channel map
            String domain = transaction.getDomain();
            Channel channel = ctx.channel();
            registerChannel(domain, channel);
        }

        RemotingCommand remotingCommand = RemotingCommand.createServiceResponseCommand(null);
        remotingCommand.setBody(new byte[]{(byte) result});
        return remotingCommand;
    }

    private void doRegister(String domain, Channel channel) {
        registerDomain(domain);
        registerRecoveryTask(domain);
        registerChannel(domain, channel);
    }

    public void registerDomain(String domain) {
        try {
            if (transactionStorage.supportStorageRecoverable()) {
                ((StorageRecoverable) transactionStorage).registerDomain(new DomainStore(domain));
            } else {
                logger.warn("transactionStorage:{} not StorageRecoverable, do not regist domain", transactionStorage.getClass().getSimpleName());
            }
        } catch (Exception e) {
            logger.error("register domain:{} error", domain, e);
        }
    }

    public void registerRecoveryTask(String domain) {
        scheduler.registerScheduleAndStartIfNotPresent(domain);
    }

    public void registerChannel(String domain, Channel channel) {
        FactoryBuilder.factoryOf(ChannelGroupMap.class).getInstance().registerChannel(domain, channel);
    }

}
