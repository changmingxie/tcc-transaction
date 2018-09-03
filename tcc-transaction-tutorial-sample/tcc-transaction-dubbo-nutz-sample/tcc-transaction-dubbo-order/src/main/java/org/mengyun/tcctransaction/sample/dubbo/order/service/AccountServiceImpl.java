package org.mengyun.tcctransaction.sample.dubbo.order.service;

import java.math.BigDecimal;

import org.mengyun.tcctransaction.nutz.support.ServiceFactory;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalAccountService;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.api.RedPacketAccountService;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
@IocBean
public class AccountServiceImpl {

/*    @Inject
    RedPacketAccountService redPacketAccountService;

    @Inject
    CapitalAccountService capitalAccountService;*/


    public BigDecimal getRedPacketAccountByUserId(long userId){
    	RedPacketAccountService redPacketAccountService = ServiceFactory.getService(RedPacketAccountService.class, null);
        return redPacketAccountService.getRedPacketAccountByUserId(userId);
    }

    public BigDecimal getCapitalAccountByUserId(long userId){
    	CapitalAccountService capitalAccountService = ServiceFactory.getService(CapitalAccountService.class, null);
        return capitalAccountService.getCapitalAccountByUserId(userId);
    }
}
