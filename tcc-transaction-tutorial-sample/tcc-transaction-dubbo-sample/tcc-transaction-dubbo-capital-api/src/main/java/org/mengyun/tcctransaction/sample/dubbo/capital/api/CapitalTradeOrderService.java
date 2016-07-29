package org.mengyun.tcctransaction.sample.dubbo.capital.api;

import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface CapitalTradeOrderService {

    public String record(TransactionContext transactionContext,CapitalTradeOrderDto tradeOrderDto);
}
