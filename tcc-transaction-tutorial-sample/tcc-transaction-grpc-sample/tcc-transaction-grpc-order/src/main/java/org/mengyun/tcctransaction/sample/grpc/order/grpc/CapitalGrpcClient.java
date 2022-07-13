package org.mengyun.tcctransaction.sample.grpc.order.grpc;

import io.grpc.Channel;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;
import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceGrpc;
import org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass;
import org.mengyun.tcctransaction.sample.grpc.capital.api.dto.CapitalTradeOrderDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author Nervose.Wu
 * @date 2022/6/24 11:20
 */
@Service
public class CapitalGrpcClient {

    @GrpcClient("capital")
    private Channel capitalChannel;

    public BigDecimal getCapitalAccountByUserId(long userId) {
        CapitalServiceGrpc.CapitalServiceBlockingStub stub = CapitalServiceGrpc.newBlockingStub(capitalChannel);
        CapitalServiceOuterClass.CapitalAccountReply capitalAccountReply = stub.getCapitalAccountByUserId(CapitalServiceOuterClass.CapitalAccountRequest.newBuilder().setUserId(userId).build());
        return new BigDecimal(capitalAccountReply.getAmount());
    }

    @EnableTcc
    public String record(CapitalTradeOrderDto tradeOrderDto) {
        CapitalServiceOuterClass.CapitalTradeOrderDto capitalTradeOrderDto = CapitalServiceOuterClass.CapitalTradeOrderDto.newBuilder()
                .setSelfUserId(tradeOrderDto.getSelfUserId())
                .setOppositeUserId(tradeOrderDto.getOppositeUserId())
                .setOrderTitle(tradeOrderDto.getOrderTitle())
                .setMerchantOrderNo(tradeOrderDto.getMerchantOrderNo())
                .setAmount(tradeOrderDto.getAmount().toPlainString())
                .build();

        CapitalServiceGrpc.CapitalServiceBlockingStub stub = CapitalServiceGrpc.newBlockingStub(capitalChannel);
        return stub.record(capitalTradeOrderDto).getMessage();
    }
}
