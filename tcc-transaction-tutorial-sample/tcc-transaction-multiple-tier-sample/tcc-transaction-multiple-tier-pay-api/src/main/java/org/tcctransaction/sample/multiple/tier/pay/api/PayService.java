package org.tcctransaction.sample.multiple.tier.pay.api;

import org.mengyun.tcctransaction.api.EnableTcc;

public interface PayService {
    @EnableTcc
    void deduct();
}
