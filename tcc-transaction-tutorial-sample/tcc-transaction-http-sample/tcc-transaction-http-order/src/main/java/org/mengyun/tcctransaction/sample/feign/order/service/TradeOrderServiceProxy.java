package org.mengyun.tcctransaction.sample.feign.order.service;

import org.mengyun.tcctransaction.sample.feign.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.feign.order.feign.CapitalFeignClient;
import org.mengyun.tcctransaction.sample.feign.order.feign.RedPacketFeignClient;
import org.mengyun.tcctransaction.sample.feign.redpacket.api.dto.RedPacketTradeOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:38
 */
@Component
public class TradeOrderServiceProxy {

    @Autowired
    private CapitalFeignClient capitalFeignClient;

    @Autowired
    private RedPacketFeignClient redPacketFeignClient;

    public String record(CapitalTradeOrderDto tradeOrderDto) {
        return capitalFeignClient.record(tradeOrderDto);
    }

    public String record(RedPacketTradeOrderDto tradeOrderDto) {
        return redPacketFeignClient.record(tradeOrderDto);
    }
}
