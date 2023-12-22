package org.mengyun.tcctransaction.sample.feign.order.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.sample.feign.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.feign.redpacket.api.dto.RedPacketTradeOrderDto;
import org.mengyun.tcctransaction.sample.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.order.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.SocketTimeoutException;
import java.util.Calendar;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:38
 */
@Service
public class PaymentServiceImpl {

    @Autowired
    private TradeOrderServiceProxy tradeOrderServiceProxy;

    @Autowired
    private OrderRepository orderRepository;


    @Compensable(confirmMethod = "confirmMakePayment", cancelMethod = "cancelMakePayment", asyncConfirm = true, delayCancelExceptions = {SocketTimeoutException.class})
    @Transactional
    public void makePayment(String orderNo) {

        System.out.println("order try make payment called.time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        Order order = orderRepository.findByMerchantOrderNo(orderNo);

        String result = tradeOrderServiceProxy.record(buildCapitalTradeOrderDto(order));
        String result2 = tradeOrderServiceProxy.record(buildRedPacketTradeOrderDto(order));

        if (order.getRedPacketPayAmount().intValue() % 10 == 0) {
            throw new RuntimeException();
        }

//        String result = tradeOrderServiceProxy.record(null,buildCapitalTradeOrderDto(order));
//        String result2 = tradeOrderServiceProxy.record(null,buildRedPacketTradeOrderDto(order));
    }

    public void confirmMakePayment(String orderNo) {

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("order confirm make payment called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        Order foundOrder = orderRepository.findByMerchantOrderNo(orderNo);

        //check order status, only if the status equals DRAFT, then confirm order
        if (foundOrder != null) {
            foundOrder.confirm();
            orderRepository.update(foundOrder);
        }
    }

    public void cancelMakePayment(String orderNo) {

        Order foundOrder = orderRepository.findByMerchantOrderNo(orderNo);

        if (foundOrder != null && foundOrder.getRedPacketPayAmount().intValue() % 20 == 0) {
            throw new RuntimeException();
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("order cancel make payment called.time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        if (foundOrder != null) {
            foundOrder.cancelPayment();
            orderRepository.update(foundOrder);
        }
    }


    private CapitalTradeOrderDto buildCapitalTradeOrderDto(Order order) {

        CapitalTradeOrderDto tradeOrderDto = new CapitalTradeOrderDto();
        tradeOrderDto.setAmount(order.getCapitalPayAmount());
        tradeOrderDto.setMerchantOrderNo(order.getMerchantOrderNo());
        tradeOrderDto.setSelfUserId(order.getPayerUserId());
        tradeOrderDto.setOppositeUserId(order.getPayeeUserId());
        tradeOrderDto.setOrderTitle(String.format("order no:%s", order.getMerchantOrderNo()));

        return tradeOrderDto;
    }

    private RedPacketTradeOrderDto buildRedPacketTradeOrderDto(Order order) {
        RedPacketTradeOrderDto tradeOrderDto = new RedPacketTradeOrderDto();
        tradeOrderDto.setAmount(order.getRedPacketPayAmount());
        tradeOrderDto.setMerchantOrderNo(order.getMerchantOrderNo());
        tradeOrderDto.setSelfUserId(order.getPayerUserId());
        tradeOrderDto.setOppositeUserId(order.getPayeeUserId());
        tradeOrderDto.setOrderTitle(String.format("order no:%s", order.getMerchantOrderNo()));

        return tradeOrderDto;
    }
}
