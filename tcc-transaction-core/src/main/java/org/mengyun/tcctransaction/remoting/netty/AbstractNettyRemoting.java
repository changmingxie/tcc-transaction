package org.mengyun.tcctransaction.remoting.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.epoll.Epoll;
import org.apache.commons.lang3.tuple.Pair;
import org.mengyun.tcctransaction.remoting.RequestProcessor;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;
import org.mengyun.tcctransaction.stats.StatsManager;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public abstract class AbstractNettyRemoting {

    private static final Logger logger = LoggerFactory.getLogger(AbstractNettyRemoting.class);

    protected final HashMap<Integer /* serviceCode */, Pair<RequestProcessor<ChannelHandlerContext>, ExecutorService>> processorTable =
            new HashMap<>(64);
    protected Pair<RequestProcessor<ChannelHandlerContext>, ExecutorService> defaultRequestProcessor;

    protected StatsManager statsManager;

    protected ConcurrentMap<Integer /* requestId */, ResponseFuture> responseTable = new ConcurrentHashMap<>(256);

    protected void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand cmd) {
        if (cmd.isRequestCommand()) {
            processRequestCommand(ctx, cmd);
        } else {
            processResponseCommand(cmd);
        }
    }

    private void processResponseCommand(RemotingCommand cmd) {

        final int opaqueId = cmd.getRequestId();
        final ResponseFuture responseFuture = responseTable.get(opaqueId);
        if (responseFuture != null) {
            responseTable.remove(opaqueId);
            responseFuture.putResponse(cmd);
        }
    }

    private void processRequestCommand(ChannelHandlerContext ctx, RemotingCommand cmd) {
        Pair<RequestProcessor<ChannelHandlerContext>, ExecutorService> matched = this.processorTable.get(cmd.getServiceCode());
        Pair<RequestProcessor<ChannelHandlerContext>, ExecutorService> pair = matched != null ? matched : this.defaultRequestProcessor;

        Runnable run = () -> {
            try {
                RemotingCommand responseCommand = pair.getLeft().processRequest(ctx, cmd);
                responseCommand.setOriginalCommand(cmd);
                responseCommand.setCode(RemotingCommandCode.SERVICE_RESP);
                responseCommand.setRequestId(cmd.getRequestId());
                ctx.writeAndFlush(responseCommand);

            } catch (Exception e) {
                logger.error("processRequest failed, command code:{}, service code:{}", cmd.getCode(), cmd.getServiceCode(), e);

                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                RemotingCommand responseCommand = RemotingCommand.createCommand(RemotingCommandCode.SYSTEM_EXCEPTION_RESP, errors.toString());
                responseCommand.setOriginalCommand(cmd);
                responseCommand.setRequestId(cmd.getRequestId());
                ctx.writeAndFlush(responseCommand);
            }
        };

        try {
            pair.getRight().submit(run);
        } catch (RejectedExecutionException e) {
            if ((System.currentTimeMillis() % 10000) == 0) {
                logger.error("{},too many requests and system thread pool busy, RejectedExecutionException request code:{}",
                        NetUtils.parseSocketAddress(ctx.channel().remoteAddress()), cmd.getCode());
            }

            final RemotingCommand response = RemotingCommand.createCommand(RemotingCommandCode.SYSTEM_BUSY_RESP,
                    "[OVERLOAD]system busy, start flow control for a while");
            response.setOriginalCommand(cmd);
            response.setRequestId(cmd.getRequestId());
            ctx.writeAndFlush(response);
        }
    }

    protected boolean useEpoll() {
        return Epoll.isAvailable();
    }
}
