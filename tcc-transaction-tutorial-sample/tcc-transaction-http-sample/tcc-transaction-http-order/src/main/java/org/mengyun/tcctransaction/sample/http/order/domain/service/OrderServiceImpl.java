package org.mengyun.tcctransaction.sample.http.order.domain.service;

import org.apache.commons.lang3.tuple.Pair;
import org.mengyun.tcctransaction.sample.http.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.http.order.domain.factory.OrderFactory;
import org.mengyun.tcctransaction.sample.http.order.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by changming.xie on 3/25/16.
 */
@Service
public class OrderServiceImpl {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderFactory orderFactory;

    @Transactional
    public Order createOrder(long payerUserId, long payeeUserId, List<Pair<Long, Integer>> productQuantities) {
        Order order = orderFactory.buildOrder(payerUserId, payeeUserId, productQuantities);

        orderRepository.createOrder(order);

        return order;
    }

    public String getOrderStatusByMerchantOrderNo(String orderNo){
        return orderRepository.findByMerchantOrderNo(orderNo).getStatus();
    }
}
