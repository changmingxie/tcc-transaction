package org.tcctransaction.sample.multiple.tier.trade.order.api;

import org.mengyun.tcctransaction.api.EnableTcc;

public interface OrderService {

    @EnableTcc
    void place();
}
