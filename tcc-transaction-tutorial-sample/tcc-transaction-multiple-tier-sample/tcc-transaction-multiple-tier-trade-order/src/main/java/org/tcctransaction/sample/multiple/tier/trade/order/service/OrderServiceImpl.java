package org.tcctransaction.sample.multiple.tier.trade.order.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.mengyun.tcctransaction.api.Compensable;
import org.tcctransaction.sample.multiple.tier.trade.order.api.OrderService;

import java.util.Calendar;

@DubboService
public class OrderServiceImpl implements OrderService {

    @Compensable(confirmMethod = "confirm", cancelMethod = "cancel")
    public void place() {
        System.out.println("OrderServiceImpl.place called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void confirm() {
        System.out.println("OrderServiceImpl.confirm called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void cancel() {
        System.out.println("OrderServiceImpl.cancel called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }
}
