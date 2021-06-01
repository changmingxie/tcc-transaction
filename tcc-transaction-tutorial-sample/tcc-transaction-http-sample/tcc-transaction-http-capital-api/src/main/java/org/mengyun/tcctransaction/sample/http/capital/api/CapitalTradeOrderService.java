package org.mengyun.tcctransaction.sample.http.capital.api;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.sample.http.capital.api.dto.CapitalTradeOrderDto;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface CapitalTradeOrderService {
    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto);
}
