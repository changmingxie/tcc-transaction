package org.mengyun.tcctransaction.sample.http.redpacket.domain.repository;

import org.mengyun.tcctransaction.sample.http.redpacket.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.http.redpacket.infrastructure.dao.TradeOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by twinkle.zhou on 16/11/14.
 */
@Repository
public class TradeOrderRepository {

    @Autowired
    TradeOrderDao tradeOrderDao;

    public void insert(TradeOrder tradeOrder){
        tradeOrderDao.insert(tradeOrder);
    }

    public void update(TradeOrder tradeOrder){
        tradeOrderDao.update(tradeOrder);
    }

    public TradeOrder findByMerchantOrderNo(String merchantOrderNo){
        return tradeOrderDao.findByMerchantOrderNo(merchantOrderNo);
    }

}
