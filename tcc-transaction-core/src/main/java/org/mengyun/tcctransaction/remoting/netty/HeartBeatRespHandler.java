package org.mengyun.tcctransaction.remoting.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;
import org.mengyun.tcctransaction.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RemotingCommand remotingCommand = (RemotingCommand) msg;
        if (remotingCommand.getCode() == RemotingCommandCode.HEARTBEAT_REQ) {
            if (logger.isDebugEnabled()) {
                logger.debug("received heartbeat request from addr：{}, the request id:{}", NetUtils.parseSocketAddress(ctx.channel().remoteAddress()), remotingCommand.getRequestId());
            }
            RemotingCommand responseCommand = RemotingCommand.createCommand(RemotingCommandCode.HEARTBEAT_RESP, null);
            if (logger.isDebugEnabled()) {
                logger.debug("send the heartbeat response to addr:{}, the request id:{}", NetUtils.parseSocketAddress(ctx.channel().remoteAddress()), responseCommand.getRequestId());
            }
            ctx.writeAndFlush(responseCommand);
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            logger.warn("client channel idle too long, maybe closed, close the channel");
            //            SecurityCenter.removeLoginUser(ctx.channel().remoteAddress().toString());
            ctx.close();
        }
        super.exceptionCaught(ctx, cause);
    }
}
