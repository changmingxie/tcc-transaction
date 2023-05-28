package org.tcctransaction.sample.multiple.tier.trade.point.api;

import org.mengyun.tcctransaction.api.EnableTcc;

public interface TradePointService {

    @EnableTcc
    void deduct();
}
