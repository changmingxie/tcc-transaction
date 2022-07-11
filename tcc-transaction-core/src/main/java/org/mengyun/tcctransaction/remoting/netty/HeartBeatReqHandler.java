package org.mengyun.tcctransaction.remoting.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;
import org.mengyun.tcctransaction.remoting.protocol.RemotingCommandCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatReqHandler.class);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {

                RemotingCommand requestCommand = RemotingCommand.createCommand(RemotingCommandCode.HEARTBEAT_REQ, null);

                logger.debug("heartbeat request triggered, the request id：%d", requestCommand.getRequestId());
                ctx.writeAndFlush(requestCommand);
            }
        }

        super.userEventTriggered(ctx, evt);
    }


    public void channelRead(ChannelHandlerContext ctx, Object cmd) throws Exception {
        RemotingCommand remotingCommand = (RemotingCommand) cmd;

        if (remotingCommand.getCode() == RemotingCommandCode.HEARTBEAT_RESP) {
            logger.debug("received heartbeat response, the request id：%d", remotingCommand.getRequestId());
            ReferenceCountUtil.release(cmd);
        } else {
            ctx.fireChannelRead(cmd);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            logger.warn("server channel idle too long, maybe closed, close the channel");
//            SecurityCenter.removeLoginUser(ctx.channel().remoteAddress().toString());
            ctx.close();
        }
        super.exceptionCaught(ctx, cause);
    }
}
