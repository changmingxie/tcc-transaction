package org.mengyun.tcctransaction.sample.dubbo.order.web.controller;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mengyun.tcctransaction.sample.dubbo.order.web.controller.vo.PlaceOrderRequest;
import org.mengyun.tcctransaction.sample.dubbo.order.service.PlaceOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

/**
 * Created by changming.xie on 4/1/16.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    PlaceOrderServiceImpl placeOrderService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public void placeOrder() {

        PlaceOrderRequest request = buildRequest();

        placeOrderService.placeOrder(request.getPayerUserId(), request.getShopId(),
                request.getProductQuantities(), request.getRedPacketPayAmount());
    }

    private PlaceOrderRequest buildRequest() {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setPayerUserId(1000);
        request.setShopId(1);
        request.setRedPacketPayAmount(BigDecimal.TEN);
        request.getProductQuantities().add(new ImmutablePair<Long, Integer>(1L, 1));
        return request;
    }
}
