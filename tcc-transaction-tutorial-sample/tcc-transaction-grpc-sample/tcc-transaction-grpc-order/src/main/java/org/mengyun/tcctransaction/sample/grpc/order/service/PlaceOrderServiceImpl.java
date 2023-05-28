package org.mengyun.tcctransaction.sample.grpc.order.service;

import org.apache.commons.lang3.tuple.Pair;
import org.mengyun.tcctransaction.exception.CancellingException;
import org.mengyun.tcctransaction.exception.ConfirmingException;
import org.mengyun.tcctransaction.sample.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.order.domain.entity.Shop;
import org.mengyun.tcctransaction.sample.order.domain.repository.ShopRepository;
import org.mengyun.tcctransaction.sample.order.domain.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:38
 */
@Service
public class PlaceOrderServiceImpl {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private PaymentServiceImpl paymentService;

    public String placeOrder(long payerUserId, long shopId, List<Pair<Long, Integer>> productQuantities, BigDecimal redPacketPayAmount) {
        Shop shop = shopRepository.findById(shopId);
        Order order = orderService.createOrder(payerUserId, shop.getOwnerUserId(), productQuantities);
        order.needToPay(redPacketPayAmount, order.getTotalAmount().subtract(redPacketPayAmount));
        orderService.update(order);
        Boolean result = false;
        try {
            paymentService.makePayment(order.getMerchantOrderNo());
        } catch (ConfirmingException confirmingException) {
            //exception throws with the tcc transaction status is CONFIRMING,
            //when tcc transaction is confirming status,
            // the tcc transaction recovery will try to confirm the whole transaction to ensure eventually consistent.
            result = true;
        } catch (CancellingException cancellingException) {
            //exception throws with the tcc transaction status is CANCELLING,
            //when tcc transaction is under CANCELLING status,
            // the tcc transaction recovery will try to cancel the whole transaction to ensure eventually consistent.
        } catch (Throwable e) {
            //other exceptions throws at TRYING stage.
            //you can retry or cancel the operation.
            e.printStackTrace();
        }
        return order.getMerchantOrderNo();
    }
}
