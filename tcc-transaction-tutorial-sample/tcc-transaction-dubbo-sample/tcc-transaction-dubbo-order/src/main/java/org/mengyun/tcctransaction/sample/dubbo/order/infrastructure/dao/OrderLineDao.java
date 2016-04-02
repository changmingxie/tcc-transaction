package org.mengyun.tcctransaction.sample.dubbo.order.infrastructure.dao;

import org.mengyun.tcctransaction.sample.dubbo.order.domain.entity.OrderLine;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface OrderLineDao {
    void insert(OrderLine orderLine);
}
