package org.mengyun.tcctransaction.sample.dubbo.order.service;

import org.apache.commons.lang3.tuple.Pair;
import org.mengyun.tcctransaction.sample.dubbo.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.dubbo.order.domain.entity.Shop;
import org.mengyun.tcctransaction.sample.dubbo.order.domain.service.OrderServiceImpl;
import org.mengyun.tcctransaction.sample.dubbo.order.domain.service.PaymentServiceImpl;
import org.mengyun.tcctransaction.sample.dubbo.order.domain.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by changming.xie on 4/1/16.
 */
@Service
public class PlaceOrderServiceImpl {

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    PaymentServiceImpl paymentService;

    public void placeOrder(long payerUserId, long shopId, List<Pair<Long, Integer>> productQuantities,BigDecimal redPacketPayAmount) {
        Shop shop = shopRepository.findById(shopId);
        Order order = orderService.createOrder(payerUserId,shop.getOwnerUserId(),productQuantities);
        paymentService.makePayment(order,redPacketPayAmount,order.getTotalAmount().subtract(redPacketPayAmount));
    }
}
