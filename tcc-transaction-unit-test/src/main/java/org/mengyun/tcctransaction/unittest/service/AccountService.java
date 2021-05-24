package org.mengyun.tcctransaction.unittest.service;

import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * Created by changmingxie on 10/25/15.
 */
public interface AccountService {

    void transferTo(TransactionContext transactionContext, long accountId, int amount);

    void transferFrom(TransactionContext transactionContext, long accountId, int amount);
}
