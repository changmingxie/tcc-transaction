package org.mengyun.tcctransaction.sample.http.order.domain.repository;

import org.mengyun.tcctransaction.sample.http.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.http.order.infrastructure.dao.OrderDao;
import org.mengyun.tcctransaction.sample.http.order.infrastructure.dao.OrderLineDao;
import org.mengyun.tcctransaction.sample.http.order.domain.entity.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by changming.xie on 4/1/16.
 */
@Repository
public class OrderRepository {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderLineDao orderLineDao;

    public void createOrder(Order order) {
        orderDao.insert(order);

        for(OrderLine orderLine:order.getOrderLines()) {
            orderLineDao.insert(orderLine);
        }
    }

    public void updateOrder(Order order) {
        orderDao.update(order);
    }

    public Order findByMerchantOrderNo(String merchantOrderNo){
        return orderDao.findByMerchantOrderNo(merchantOrderNo);
    }
}
