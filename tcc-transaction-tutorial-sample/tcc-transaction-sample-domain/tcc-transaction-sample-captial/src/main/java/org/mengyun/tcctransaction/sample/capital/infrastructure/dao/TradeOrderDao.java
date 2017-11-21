package org.mengyun.tcctransaction.sample.capital.infrastructure.dao;


import org.mengyun.tcctransaction.sample.capital.domain.entity.TradeOrder;

/**
 * Created by twinkle.zhou on 16/11/14.
 */
public interface TradeOrderDao {

    int insert(TradeOrder tradeOrder);

    int update(TradeOrder tradeOrder);

    TradeOrder findByMerchantOrderNo(String merchantOrderNo);
}
