package org.mengyun.tcctransaction.sample.dubbo.order.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.mengyun.tcctransaction.CancellingException;
import org.mengyun.tcctransaction.ConfirmingException;
import org.mengyun.tcctransaction.sample.exception.OptimisticLockingFailureException;
import org.mengyun.tcctransaction.sample.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.order.domain.entity.Shop;
import org.mengyun.tcctransaction.sample.order.domain.repository.ShopRepository;
import org.mengyun.tcctransaction.sample.order.domain.service.OrderServiceImpl;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Created by changming.xie on 4/1/16.
 */
@IocBean
public class PlaceOrderServiceImpl {

    @Inject
    ShopRepository shopRepository;

    @Inject
    OrderServiceImpl orderServiceImpl;

    @Inject
    PaymentServiceImpl paymentServiceImpl;

    public String placeOrder(long payerUserId, long shopId, List<Pair<Long, Integer>> productQuantities, BigDecimal redPacketPayAmount) {
        Shop shop = shopRepository.findById(shopId);
        
        Order order = orderServiceImpl.createOrder(payerUserId, shop.getOwnerUserId(), productQuantities);

        Boolean result = false;

        try {

            paymentServiceImpl.makePayment(order, redPacketPayAmount, order.getTotalAmount().subtract(redPacketPayAmount));
            
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
