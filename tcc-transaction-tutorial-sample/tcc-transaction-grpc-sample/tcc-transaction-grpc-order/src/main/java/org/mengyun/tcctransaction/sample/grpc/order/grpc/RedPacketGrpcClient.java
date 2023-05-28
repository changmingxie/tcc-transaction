package org.mengyun.tcctransaction.sample.grpc.order.grpc;

import io.grpc.Channel;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceGrpc;
import org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass;
import org.mengyun.tcctransaction.sample.grpc.redpacket.api.dto.RedPacketTradeOrderDto;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

/**
 * @author Nervose.Wu
 * @date 2022/6/24 11:21
 */
@Service
public class RedPacketGrpcClient {

    @GrpcClient("redpacket")
    private Channel redPacketChannel;

    public BigDecimal getRedPacketAccountByUserId(long userId) {
        RedPacketServiceGrpc.RedPacketServiceBlockingStub stub = RedPacketServiceGrpc.newBlockingStub(redPacketChannel);
        RedPacketServiceOuterClass.RedPacketAccountReply redPacketAccountReply = stub.getRedPacketAccountByUserId(RedPacketServiceOuterClass.RedPacketAccountRequest.newBuilder().setUserId(userId).build());
        return new BigDecimal(redPacketAccountReply.getAmount());
    }

    @EnableTcc
    public String record(RedPacketTradeOrderDto tradeOrderDto) {
        RedPacketServiceOuterClass.RedPacketTradeOrderDto redPacketTradeOrderDto = RedPacketServiceOuterClass.RedPacketTradeOrderDto.newBuilder().setSelfUserId(tradeOrderDto.getSelfUserId()).setOppositeUserId(tradeOrderDto.getOppositeUserId()).setOrderTitle(tradeOrderDto.getOrderTitle()).setMerchantOrderNo(tradeOrderDto.getMerchantOrderNo()).setAmount(tradeOrderDto.getAmount().toPlainString()).build();
        RedPacketServiceGrpc.RedPacketServiceBlockingStub stub = RedPacketServiceGrpc.newBlockingStub(redPacketChannel);
        return stub.record(redPacketTradeOrderDto).getMessage();
    }
}
