package org.tcctransaction.sample.multiple.tier.trade.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.mengyun.tcctransaction.api.Compensable;
import org.springframework.stereotype.Service;
import org.tcctransaction.sample.multiple.tier.pay.api.PayService;
import org.tcctransaction.sample.multiple.tier.trade.order.api.OrderService;
import org.tcctransaction.sample.multiple.tier.trade.point.api.TradePointService;

import java.util.Calendar;

@Service
public class TradeService {

    @DubboReference
    OrderService orderService;

    @DubboReference
    TradePointService tradePointService;

    @DubboReference
    PayService payService;

    private boolean firstRun = true;

    @Compensable
    public void place() {

        System.out.println("TradeService.place called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));

        orderService.place();

        tradePointService.deduct();

        payService.deduct();

//        throw new RuntimeException("call place at try stage failed.");
    }

}
