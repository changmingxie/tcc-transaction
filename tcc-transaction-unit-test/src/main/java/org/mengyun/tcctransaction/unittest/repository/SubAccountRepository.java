package org.mengyun.tcctransaction.unittest.repository;

import org.mengyun.tcctransaction.unittest.entity.SubAccount;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by changmingxie on 12/3/15.
 */
@Repository
public class SubAccountRepository {

    private Map<Long, SubAccount> subAccountMap = new HashMap<Long, SubAccount>();

    {
        subAccountMap.put(1L, new SubAccount(1, 100));
        subAccountMap.put(2L, new SubAccount(2, 200));
        subAccountMap.put(3L, new SubAccount(3, 300));
    }

    public SubAccount findById(Long id) {
        return subAccountMap.get(id);
    }
}
