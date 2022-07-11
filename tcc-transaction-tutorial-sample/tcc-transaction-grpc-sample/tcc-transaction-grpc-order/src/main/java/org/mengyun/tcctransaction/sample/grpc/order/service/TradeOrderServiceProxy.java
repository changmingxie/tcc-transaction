package org.mengyun.tcctransaction.sample.grpc.order.service;


import org.mengyun.tcctransaction.sample.grpc.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.grpc.order.grpc.CapitalGrpcClient;
import org.mengyun.tcctransaction.sample.grpc.order.grpc.RedPacketGrpcClient;
import org.mengyun.tcctransaction.sample.grpc.redpacket.api.dto.RedPacketTradeOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:38
 */
@Component
public class TradeOrderServiceProxy {

    @Autowired
    private CapitalGrpcClient capitalGrpcClient;

    @Autowired
    private RedPacketGrpcClient redPacketGrpcClient;

    public String record(CapitalTradeOrderDto tradeOrderDto) {
        return capitalGrpcClient.record(tradeOrderDto);
    }

    public String record(RedPacketTradeOrderDto tradeOrderDto) {
        return redPacketGrpcClient.record(tradeOrderDto);
    }
}
