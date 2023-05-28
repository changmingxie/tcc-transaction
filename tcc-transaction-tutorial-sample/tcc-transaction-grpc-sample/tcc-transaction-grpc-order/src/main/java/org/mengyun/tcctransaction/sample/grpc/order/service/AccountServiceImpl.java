package org.mengyun.tcctransaction.sample.grpc.order.service;

import org.mengyun.tcctransaction.sample.grpc.order.grpc.CapitalGrpcClient;
import org.mengyun.tcctransaction.sample.grpc.order.grpc.RedPacketGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:39
 */
@Service("accountService")
public class AccountServiceImpl {

    @Autowired
    private CapitalGrpcClient capitalGrpcClient;

    @Autowired
    private RedPacketGrpcClient redPacketGrpcClient;

    public BigDecimal getRedPacketAccountByUserId(long userId) {
        return redPacketGrpcClient.getRedPacketAccountByUserId(userId);
    }

    public BigDecimal getCapitalAccountByUserId(long userId) {
        return capitalGrpcClient.getCapitalAccountByUserId(userId);
    }
}
