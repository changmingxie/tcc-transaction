package org.mengyun.tcctransaction.sample.dubbo.order.web.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mengyun.tcctransaction.sample.dubbo.order.service.AccountServiceImpl;
import org.mengyun.tcctransaction.sample.dubbo.order.service.PlaceOrderServiceImpl;
import org.mengyun.tcctransaction.sample.dubbo.order.web.controller.vo.PlaceOrderRequest;
import org.mengyun.tcctransaction.sample.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.order.domain.entity.Product;
import org.mengyun.tcctransaction.sample.order.domain.repository.ProductRepository;
import org.mengyun.tcctransaction.sample.order.domain.service.OrderServiceImpl;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.ServerRedirectView;
import org.nutz.mvc.view.ViewWrapper;

/**
 * Created by changming.xie on 4/1/16.
 */
@IocBean
@At("")
public class OrderController {
	@Inject
    PlaceOrderServiceImpl placeOrderServiceImpl;
	@Inject
    ProductRepository productRepository;

    @Inject
    AccountServiceImpl accountServiceImpl;

    @Inject
    OrderServiceImpl orderServiceImpl;
    
    @GET
    @At("/")
    @Ok("fm:/ftl/index") 
    public void index() {
    	
    }
    @GET
    @At("/user/?/shop/?")
    @Ok("fm:/ftl/shop") 
    public Map<String, Object> getProductsInShop(long userId,long shopId) {
        List<Product> products = productRepository.findByShopId(shopId);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("products", products);
        map.put("userId", userId);
        map.put("shopId", shopId);

        return map;
    }
    
    @GET
    @At("/user/?/shop/?/product/?/confirm")
    @Ok("fm:/ftl/product_detail") 
    public Map<String, Object> productDetail(long userId,
                                      long shopId,
                                      long productId) {

    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("capitalAmount", accountServiceImpl.getCapitalAccountByUserId(userId));
    	map.put("redPacketAmount", accountServiceImpl.getRedPacketAccountByUserId(userId));

    	map.put("product", productRepository.findById(productId));

    	map.put("userId", userId);
    	map.put("shopId", shopId);

        return map;
    }

    @POST
    @At("/placeorder")
    public View placeOrder(@Param("redPacketPayAmount") String redPacketPayAmount,
    		@Param("shopId") long shopId,
    		@Param("payerUserId") long payerUserId,
    		@Param("productId") long productId) {


        PlaceOrderRequest request = buildRequest(redPacketPayAmount, shopId, payerUserId, productId);

        String merchantOrderNo = placeOrderServiceImpl.placeOrder(request.getPayerUserId(), request.getShopId(),
                request.getProductQuantities(), request.getRedPacketPayAmount());
        return new ViewWrapper(new ServerRedirectView("/payresult/" + merchantOrderNo), null);
    }
    
    @GET
    @At("/payresult/?")
    @Ok("fm:/ftl/pay_success") 
    public Map<String, Object> getPayResult(String merchantOrderNo) {

    	Map<String, Object> map = new HashMap<String, Object>();

        String payResultTip = null;
        Order foundOrder = orderServiceImpl.findOrderByMerchantOrderNo(merchantOrderNo);

        if ("CONFIRMED".equals(foundOrder.getStatus()))
            payResultTip = "支付成功";
        else if ("PAY_FAILED".equals(foundOrder.getStatus()))
            payResultTip = "支付失败";
        else
            payResultTip = "Unknown";

        map.put("payResult", payResultTip);

        map.put("capitalAmount", accountServiceImpl.getCapitalAccountByUserId(foundOrder.getPayerUserId()));
        map.put("redPacketAmount", accountServiceImpl.getRedPacketAccountByUserId(foundOrder.getPayerUserId()));

        return map;
    }


    private PlaceOrderRequest buildRequest(String redPacketPayAmount, long shopId, long payerUserId, long productId) {
        BigDecimal redPacketPayAmountInBigDecimal = new BigDecimal(redPacketPayAmount);
        if (redPacketPayAmountInBigDecimal.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidParameterException("invalid red packet amount :" + redPacketPayAmount);

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setPayerUserId(payerUserId);
        request.setShopId(shopId);
        request.setRedPacketPayAmount(new BigDecimal(redPacketPayAmount));
        request.getProductQuantities().add(new ImmutablePair<Long, Integer>(productId, 1));
        return request;
    }
}
