package org.mengyun.tcctransaction.sample.capital.service;

import java.math.BigDecimal;

import org.junit.Test;
import org.mengyun.tcctransaction.sample.capital.BaseTest;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;
import org.springframework.beans.factory.annotation.Autowired;

public class CapitalTradeOrderServiceImplTest extends BaseTest{
	@Autowired
	private CapitalTradeOrderService capitalTradeOrderService;
	@Test
	public void test_record(){
		CapitalTradeOrderDto tradeOrderDto = new CapitalTradeOrderDto();
		tradeOrderDto.setSelfUserId(1000);
		tradeOrderDto.setOppositeUserId(2000);
		tradeOrderDto.setMerchantOrderNo("2515145");
		tradeOrderDto.setOrderTitle("猪肉串");
		tradeOrderDto.setAmount(new BigDecimal(3333));;
		String str = capitalTradeOrderService.record(tradeOrderDto);
		System.out.println(str);
	}
}
