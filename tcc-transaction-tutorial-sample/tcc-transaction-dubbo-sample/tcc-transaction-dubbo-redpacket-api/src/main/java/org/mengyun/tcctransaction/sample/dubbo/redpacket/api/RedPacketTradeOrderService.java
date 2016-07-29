package org.mengyun.tcctransaction.sample.dubbo.redpacket.api;

import org.mengyun.tcctransaction.sample.dubbo.redpacket.api.dto.RedPacketTradeOrderDto;
import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface RedPacketTradeOrderService {

    public String record(TransactionContext transactionContext,RedPacketTradeOrderDto tradeOrderDto);
}
