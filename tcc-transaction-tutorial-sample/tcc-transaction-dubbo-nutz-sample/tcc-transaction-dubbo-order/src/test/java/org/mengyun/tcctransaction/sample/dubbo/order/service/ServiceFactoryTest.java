package org.mengyun.tcctransaction.sample.dubbo.order.service;

import org.junit.Test;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.dubbo.order.BaseTest;
import org.mengyun.tcctransaction.sample.dubbo.order.util.ServiceFactory;

/**
 *
 * @author liangcz
 * @Date   2018年8月27日 上午10:01:10
 * @version 1.0
 */
public class ServiceFactoryTest extends BaseTest{
	@Test
	public void test(){
		CapitalTradeOrderDto tradeOrderDto = new CapitalTradeOrderDto();
		tradeOrderDto.setSelfUserId(10000);
		tradeOrderDto.setOppositeUserId(20000);
		CapitalTradeOrderService capitalTradeOrderService = ServiceFactory.getService(CapitalTradeOrderService.class, null);
		String result = capitalTradeOrderService.record(tradeOrderDto);
		System.out.println(result);
	}

}
