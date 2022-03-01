package org.tcctransaction.sample.multiple.tier.pay.account.api;

import org.mengyun.tcctransaction.api.EnableTcc;

public interface PayAccountService {
    @EnableTcc
    public void deduct();
}
