package org.mengyun.tcctransaction.sample.dubbo.redpacket.infrastructure.dao;

import org.mengyun.tcctransaction.sample.dubbo.redpacket.domain.entity.TradeOrder;

/**
 * Created by twinkle.zhou on 16/11/14.
 */
public interface TradeOrderDao {

    void insert(TradeOrder tradeOrder);

    void update(TradeOrder tradeOrder);

    TradeOrder findByMerchantOrderNo(String merchantOrderNo);
}
