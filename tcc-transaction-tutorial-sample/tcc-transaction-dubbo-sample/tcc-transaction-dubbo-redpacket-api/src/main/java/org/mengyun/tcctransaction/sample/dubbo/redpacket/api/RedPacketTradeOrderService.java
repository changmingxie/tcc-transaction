package org.mengyun.tcctransaction.sample.dubbo.redpacket.api;

import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.api.dto.RedPacketTradeOrderDto;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface RedPacketTradeOrderService {

    @EnableTcc
    public String record(RedPacketTradeOrderDto tradeOrderDto);
}
