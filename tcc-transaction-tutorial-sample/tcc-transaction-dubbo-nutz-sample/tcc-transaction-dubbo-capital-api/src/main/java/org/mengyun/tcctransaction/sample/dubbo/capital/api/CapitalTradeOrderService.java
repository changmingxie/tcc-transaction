package org.mengyun.tcctransaction.sample.dubbo.capital.api;

import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;
import org.nutz.ioc.aop.Aop;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface CapitalTradeOrderService {
	@Aop({"compensableTransactionForNutzInterceptor","resourceCoordinatorForNutzInterceptor"}) 
    @Compensable
    public String record(CapitalTradeOrderDto tradeOrderDto);

}
