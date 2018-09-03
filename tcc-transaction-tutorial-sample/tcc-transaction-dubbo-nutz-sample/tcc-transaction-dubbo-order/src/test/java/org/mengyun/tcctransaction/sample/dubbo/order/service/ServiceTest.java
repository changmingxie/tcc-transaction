package org.mengyun.tcctransaction.sample.dubbo.order.service;

import java.math.BigDecimal;

import org.junit.Test;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.dubbo.order.BaseTest;
import org.mengyun.tcctransaction.sample.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.order.domain.repository.OrderRepository;
import org.nutz.ioc.loader.annotation.Inject;

/**
 *
 * @author liangcz
 * @Date   2018年8月17日 上午8:59:02
 * @version 1.0
 */
public class ServiceTest extends BaseTest{
	@Inject(optional=true)
	private OrderRepository orderRepository;
	@Test
	public void test_record() throws Exception{
		System.out.println("begin");
		Order order = orderRepository.findByMerchantOrderNo("6e28344a-11e7-4b41-9bf7-096c452a4480");
		// order.updateVersion();
		order.pay(new BigDecimal(100), new BigDecimal(100));
		orderRepository.updateOrder(order);
		System.out.println("oversion");
	}
}
