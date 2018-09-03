package org.mengyun.tcctransaction.sample.dubbo.capital.service;

import java.math.BigDecimal;

import org.mengyun.tcctransaction.sample.capital.domain.repository.CapitalAccountRepository;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalAccountService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
@IocBean(name="capitalAccountService")
public class CapitalAccountServiceImpl implements CapitalAccountService{


    @Inject
    CapitalAccountRepository capitalAccountRepository;

    @Override
    public BigDecimal getCapitalAccountByUserId(long userId) {
        return capitalAccountRepository.findByUserId(userId).getBalanceAmount();
    }
}
