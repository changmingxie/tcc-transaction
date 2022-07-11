package org.tcctransaction.sample.multiple.tier.pay.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.mengyun.tcctransaction.api.Compensable;
import org.tcctransaction.sample.multiple.tier.pay.account.api.PayAccountService;
import org.tcctransaction.sample.multiple.tier.pay.api.PayService;
import org.tcctransaction.sample.multiple.tier.pay.point.api.PayPointService;

import java.util.Calendar;

@DubboService
public class PayServiceImpl implements PayService {

    @DubboReference
    PayAccountService payAccountService;

    @DubboReference
    PayPointService payPointService;

    @Override
    @Compensable(confirmMethod = "confirm", cancelMethod = "cancel")
    public void deduct() {
        System.out.println("PayServiceImpl.place called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));

        payAccountService.deduct();

        payPointService.deduct();
    }

    public void confirm() {
        System.out.println("PayServiceImpl.confirm called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void cancel() {
        System.out.println("PayServiceImpl.cancel called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }
}
