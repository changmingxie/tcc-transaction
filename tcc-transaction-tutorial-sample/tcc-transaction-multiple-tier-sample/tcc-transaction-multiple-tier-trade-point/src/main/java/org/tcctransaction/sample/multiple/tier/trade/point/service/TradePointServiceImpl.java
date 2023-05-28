package org.tcctransaction.sample.multiple.tier.trade.point.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.mengyun.tcctransaction.api.Compensable;
import org.tcctransaction.sample.multiple.tier.trade.point.api.TradePointService;
import java.util.Calendar;

@DubboService
public class TradePointServiceImpl implements TradePointService {

    @Override
    @Compensable(confirmMethod = "confirm", cancelMethod = "cancel")
    public void deduct() {
        System.out.println("TradePointServiceImpl.deduct called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void confirm() {
        System.out.println("TradePointServiceImpl.confirm called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void cancel() {
        System.out.println("TradePointServiceImpl.cancel called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }
}
