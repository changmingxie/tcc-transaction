package org.mengyun.tcctransaction.sample.dubbo.order.service;

import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.nutz.support.ServiceFactory;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.api.RedPacketTradeOrderService;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.api.dto.RedPacketTradeOrderDto;
import org.mengyun.tcctransaction.sample.exception.OptimisticLockingFailureException;
import org.mengyun.tcctransaction.sample.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.order.domain.repository.OrderRepository;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by changming.xie on 4/1/16.
 */
@IocBean
public class PaymentServiceImpl {

/*	@Inject
    CapitalTradeOrderService capitalTradeOrderService;

	@Inject
    RedPacketTradeOrderService redPacketTradeOrderService;*/

	@Inject
    OrderRepository orderRepository;
    
	
    private Logger logger = Logger.getLogger(getClass());
    
    @Aop({"compensableTransactionForNutzInterceptor","resourceCoordinatorForNutzInterceptor"})
    @Compensable(confirmMethod = "confirmMakePayment", cancelMethod = "cancelMakePayment", asyncConfirm = true)
    public void makePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {
        logger.info("order try make payment called");


        //check if the order status is DRAFT, if no, means that another call makePayment for the same order happened, ignore this call makePayment.
        if (order.getStatus().equals("DRAFT")) {
            order.pay(redPacketPayAmount, capitalPayAmount);
            try {
                orderRepository.updateOrder(order);
                // orderBiz.updateOrder(order);
            } catch (OptimisticLockingFailureException e) {
                //ignore the concurrently update order exception, ensure idempotency.
            }
        } // throw new RuntimeException("xx");
        
        CapitalTradeOrderService capitalTradeOrderService = ServiceFactory.getService(CapitalTradeOrderService.class, null);
        RedPacketTradeOrderService redPacketTradeOrderService = ServiceFactory.getService(RedPacketTradeOrderService.class, null);
        String result = capitalTradeOrderService.record(buildCapitalTradeOrderDto(order));
        String result2 = redPacketTradeOrderService.record(buildRedPacketTradeOrderDto(order));
        int i = 1;
    }

    public void confirmMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {


        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } // org.mengyun.tcctransaction.dubbo.util.ExceptionUtil.makeExcetion();

        logger.info("order confirm make payment called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        Order foundOrder = orderRepository.findByMerchantOrderNo(order.getMerchantOrderNo());

        //check if the trade order status is PAYING, if no, means another call confirmMakePayment happened, return directly, ensure idempotency.
        if (foundOrder != null && foundOrder.getStatus().equals("PAYING")) {
        	order.confirm();
        	order.updateVersion();
            orderRepository.updateOrder(order);
        }
    }

    public void cancelMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {

        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.info("order cancel make payment called");

        Order foundOrder = orderRepository.findByMerchantOrderNo(order.getMerchantOrderNo());

        //check if the trade order status is PAYING, if no, means another call cancelMakePayment happened, return directly, ensure idempotency.
        if (foundOrder != null && foundOrder.getStatus().equals("PAYING")) {
            order.cancelPayment();
            order.updateVersion();
            orderRepository.updateOrder(order);
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
