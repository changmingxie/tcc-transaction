package org.mengyun.tcctransaction.sample.http.capital.domain.repository;

import org.mengyun.tcctransaction.sample.http.capital.infrastructure.dao.CapitalAccountDao;
import org.mengyun.tcctransaction.sample.http.capital.domain.entity.CapitalAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by changming.xie on 4/2/16.
 */
public class CapitalAccountRepository {

    @Autowired
    CapitalAccountDao capitalAccountDao;

    public CapitalAccount findByUserId(long userId) {

        return capitalAccountDao.findByUserId(userId);
    }

    public void save(CapitalAccount capitalAccount) {
        capitalAccountDao.update(capitalAccount);
    }
}
