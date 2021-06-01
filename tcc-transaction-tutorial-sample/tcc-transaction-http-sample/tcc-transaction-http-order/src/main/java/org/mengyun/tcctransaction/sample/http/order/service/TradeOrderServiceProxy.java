package org.mengyun.tcctransaction.sample.http.order.service;

import org.mengyun.tcctransaction.sample.http.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.sample.http.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.http.redpacket.api.RedPacketTradeOrderService;
import org.mengyun.tcctransaction.sample.http.redpacket.api.dto.RedPacketTradeOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by changming.xie on 4/19/17.
 */
@Component
public class TradeOrderServiceProxy {

    @Autowired
    CapitalTradeOrderService capitalTradeOrderService;

    @Autowired
    RedPacketTradeOrderService redPacketTradeOrderService;

    /*the propagation need set Propagation.SUPPORTS,otherwise the recover doesn't work,
      The default value is Propagation.REQUIRED, which means will begin new transaction when recover.
    */
//    @Deprecated
//    @Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "record", cancelMethod = "record", transactionContextEditor = MethodTransactionContextEditor.class)
//    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
//        return capitalTradeOrderService.record(transactionContext, tradeOrderDto);
//    }
//
//    @Deprecated
//    @Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "record", cancelMethod = "record", transactionContextEditor = MethodTransactionContextEditor.class)
//    public String record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
//        return redPacketTradeOrderService.record(transactionContext, tradeOrderDto);
//    }

    public String record(CapitalTradeOrderDto tradeOrderDto) {
        return capitalTradeOrderService.record(null, tradeOrderDto);
    }

    public String record(RedPacketTradeOrderDto tradeOrderDto) {
        return redPacketTradeOrderService.record(null, tradeOrderDto);
    }


//    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
//        //return capitalTradeOrderService.record(null, tradeOrderDto);
//        return "success";
//    }
//
//    public String record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
//        //return redPacketTradeOrderService.record(null, tradeOrderDto);
//        return "success";
//    }
}
