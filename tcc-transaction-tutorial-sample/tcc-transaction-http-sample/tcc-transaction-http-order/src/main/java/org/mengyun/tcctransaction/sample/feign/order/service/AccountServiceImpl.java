package org.mengyun.tcctransaction.sample.feign.order.service;

import org.mengyun.tcctransaction.sample.feign.order.feign.CapitalFeignClient;
import org.mengyun.tcctransaction.sample.feign.order.feign.RedPacketFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:39
 */
@Service("accountService")
public class AccountServiceImpl {

    @Autowired
    private RedPacketFeignClient redPacketFeignClient;

    @Autowired
    private CapitalFeignClient capitalFeignClient;

    public BigDecimal getRedPacketAccountByUserId(long userId) {
        return redPacketFeignClient.getRedPacketAccountByUserId(userId);
    }

    public BigDecimal getCapitalAccountByUserId(long userId) {
        return capitalFeignClient.getCapitalAccountByUserId(userId);
    }
}
