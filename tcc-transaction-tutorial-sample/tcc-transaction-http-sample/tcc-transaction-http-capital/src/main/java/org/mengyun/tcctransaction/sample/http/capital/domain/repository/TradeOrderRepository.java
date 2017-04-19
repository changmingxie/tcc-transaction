package org.mengyun.tcctransaction.sample.http.capital.domain.repository;

import org.mengyun.tcctransaction.sample.http.capital.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.http.capital.infrastructure.dao.TradeOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by twinkle.zhou on 16/11/14.
 */
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
