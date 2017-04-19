package org.mengyun.tcctransaction.sample.http.capital.infrastructure.dao;

import org.mengyun.tcctransaction.sample.http.capital.domain.entity.TradeOrder;

/**
 * Created by twinkle.zhou on 16/11/14.
 */
public interface TradeOrderDao {

    void insert(TradeOrder tradeOrder);

    void update(TradeOrder tradeOrder);

    TradeOrder findByMerchantOrderNo(String merchantOrderNo);
}
