package org.mengyun.tcctransaction.sample.capital.domain.repository;

import org.mengyun.tcctransaction.sample.capital.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.capital.infrastructure.dao.TradeOrderDao;
import org.mengyun.tcctransaction.sample.exception.OptimisticLockingFailureException;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by twinkle.zhou on 16/11/14.
 */
@IocBean
public class TradeOrderRepository {

    @Inject
    TradeOrderDao tradeOrderDao;

    public void insert(TradeOrder tradeOrder) {
        tradeOrderDao.insert(tradeOrder);
    }

    public void update(TradeOrder tradeOrder) {
        // tradeOrder.updateVersion();
        int effectCount = tradeOrderDao.update(tradeOrder);
        if (effectCount < 1) {
           throw new OptimisticLockingFailureException("update trade order failed");
        }
    }

    public TradeOrder findByMerchantOrderNo(String merchantOrderNo) {
        return tradeOrderDao.findByMerchantOrderNo(merchantOrderNo);
    }

}
