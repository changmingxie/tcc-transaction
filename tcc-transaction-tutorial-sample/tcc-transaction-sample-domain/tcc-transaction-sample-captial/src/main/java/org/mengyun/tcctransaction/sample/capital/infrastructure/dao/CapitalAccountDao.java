package org.mengyun.tcctransaction.sample.capital.infrastructure.dao;

import org.mengyun.tcctransaction.sample.capital.domain.entity.CapitalAccount;

/**
 * Created by changming.xie on 4/2/16.
 */
public interface CapitalAccountDao {

    CapitalAccount findByUserId(long userId);

    int update(CapitalAccount capitalAccount);
}
