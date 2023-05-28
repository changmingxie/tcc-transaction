package org.mengyun.tcctransaction.sample.dubbo.capital.api;

import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface CapitalTradeOrderService {

    @EnableTcc
    public String record(CapitalTradeOrderDto tradeOrderDto);
}
