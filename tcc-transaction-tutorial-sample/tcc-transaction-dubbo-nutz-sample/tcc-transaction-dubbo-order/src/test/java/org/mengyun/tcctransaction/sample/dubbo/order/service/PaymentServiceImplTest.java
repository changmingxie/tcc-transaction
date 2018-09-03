package org.mengyun.tcctransaction.sample.dubbo.order.service;

import java.math.BigDecimal;

import org.junit.Test;
import org.mengyun.tcctransaction.sample.dubbo.order.BaseTest;
import org.mengyun.tcctransaction.sample.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.order.domain.repository.OrderRepository;
import org.nutz.ioc.loader.annotation.Inject;

/**
 *
 * @author liangcz
 * @Date   2018年8月23日 下午4:08:22
 * @version 1.0
 */
public class PaymentServiceImplTest extends BaseTest{
	@Inject
	private PaymentServiceImpl paymentServiceImpl;
	@Inject(optional=true)
	private OrderRepository orderRepository;
	@Test
	public void test(){
		System.out.println();
		Order order = orderRepository.findByMerchantOrderNo("51007620-8227-4b45-9bed-dc0e7d9f6b3e");
		paymentServiceImpl.makePayment(order, new BigDecimal(100), new BigDecimal(100));
		System.out.println();
	}
}
