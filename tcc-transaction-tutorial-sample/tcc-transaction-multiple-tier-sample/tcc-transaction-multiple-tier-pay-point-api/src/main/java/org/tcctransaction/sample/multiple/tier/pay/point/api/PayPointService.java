package org.tcctransaction.sample.multiple.tier.pay.point.api;

import org.mengyun.tcctransaction.api.EnableTcc;

public interface PayPointService {

    @EnableTcc
    void deduct();
}
