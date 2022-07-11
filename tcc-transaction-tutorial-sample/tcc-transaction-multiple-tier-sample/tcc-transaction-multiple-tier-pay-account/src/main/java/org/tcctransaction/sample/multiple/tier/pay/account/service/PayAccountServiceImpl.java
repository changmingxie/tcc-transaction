package org.tcctransaction.sample.multiple.tier.pay.account.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.mengyun.tcctransaction.api.Compensable;
import org.tcctransaction.sample.multiple.tier.pay.account.api.PayAccountService;

import java.util.Calendar;

@DubboService
public class PayAccountServiceImpl implements PayAccountService {

    @Compensable(confirmMethod = "confirm", cancelMethod = "cancel")
    public void deduct() {
        System.out.println("PayAccountServiceImpl.deduct called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void confirm() {
        System.out.println("PayAccountServiceImpl.confirm called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void cancel() {
        System.out.println("PayAccountServiceImpl.cancel called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }
}
