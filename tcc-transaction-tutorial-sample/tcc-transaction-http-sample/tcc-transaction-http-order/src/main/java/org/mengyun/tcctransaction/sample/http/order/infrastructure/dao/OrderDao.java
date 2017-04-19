package org.mengyun.tcctransaction.sample.http.order.infrastructure.dao;

import org.mengyun.tcctransaction.sample.http.order.domain.entity.Order;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface OrderDao {

    public void insert(Order order);

    public void update(Order order);

    Order findByMerchantOrderNo(String merchantOrderNo);
}
