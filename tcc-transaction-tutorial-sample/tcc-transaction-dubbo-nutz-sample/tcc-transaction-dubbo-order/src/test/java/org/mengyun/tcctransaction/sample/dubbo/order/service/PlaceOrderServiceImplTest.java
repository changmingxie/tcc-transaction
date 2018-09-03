package org.mengyun.tcctransaction.sample.dubbo.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.mengyun.tcctransaction.sample.dubbo.order.BaseTest;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.trans.Trans;

/**
 *
 * @author liangcz
 * @Date   2018年8月23日 下午4:45:55
 * @version 1.0
 */
public class PlaceOrderServiceImplTest extends BaseTest{
	@Inject
	PlaceOrderServiceImpl placeOrderServiceImpl;
	@Test
	public void test(){
		// Trans.DEBUG = true;
		// productQuantities
		List<Pair<Long, Integer>> productQuantities = new ArrayList<Pair<Long,Integer>>();
		Pair<Long,Integer> pair = new Pair<Long, Integer>() {
			
			@Override
			public Integer setValue(Integer arg0) {
				return 1;
			}
			
			@Override
			public Integer getRight() {
				return 2;
			}
			
			@Override
			public Long getLeft() {
				return 3l;
			}
		};
		productQuantities.add(pair);
		placeOrderServiceImpl.placeOrder(2000, 1, productQuantities, new BigDecimal(100));
		System.out.println("end");
	}
}
