package org.tcctransaction.sample.multiple.tier.pay.point.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.dubbo.context.DubboTransactionContextEditor;
import org.tcctransaction.sample.multiple.tier.pay.point.api.PayPointService;

import java.util.Calendar;

@DubboService
public class PayPointServiceImpl implements PayPointService {

    @Compensable(confirmMethod = "confirm", cancelMethod = "cancel", transactionContextEditor = DubboTransactionContextEditor.class)
    public void deduct() {
        System.out.println("PayPointServiceImpl.deduct called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void confirm() {
        System.out.println("PayPointServiceImpl.confirm called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }

    public void cancel() {
        System.out.println("PayPointServiceImpl.cancel called at : " + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
    }
}
