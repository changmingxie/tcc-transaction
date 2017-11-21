package org.mengyun.tcctransaction.sample.redpacket.domain.repository;

import org.mengyun.tcctransaction.sample.redpacket.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.redpacket.infrastructure.dao.TradeOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

/**
 * Created by twinkle.zhou on 16/11/14.
 */
@Repository
public class TradeOrderRepository {

    @Autowired
    TradeOrderDao tradeOrderDao;

    public void insert(TradeOrder tradeOrder) {
        tradeOrderDao.insert(tradeOrder);
    }

    public void update(TradeOrder tradeOrder) {

        tradeOrder.updateVersion();
        int effectCount = tradeOrderDao.update(tradeOrder);
        if (effectCount < 1) {
            throw new OptimisticLockingFailureException("update trade order failed");
        }
    }

    public TradeOrder findByMerchantOrderNo(String merchantOrderNo) {
        return tradeOrderDao.findByMerchantOrderNo(merchantOrderNo);
    }

}
