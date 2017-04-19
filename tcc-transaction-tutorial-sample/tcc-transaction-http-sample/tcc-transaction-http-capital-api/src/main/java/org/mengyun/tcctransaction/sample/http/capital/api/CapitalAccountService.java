package org.mengyun.tcctransaction.sample.http.capital.api;

import java.math.BigDecimal;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
public interface CapitalAccountService {

    BigDecimal getCapitalAccountByUserId(long userId);
}
