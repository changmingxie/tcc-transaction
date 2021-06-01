package org.mengyun.tcctransaction.sample.order.infrastructure.dao;

import org.mengyun.tcctransaction.sample.order.domain.entity.Order;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface OrderDao {

    public int insert(Order order);

    public int update(Order order);

    Order findByMerchantOrderNo(String merchantOrderNo);
}
