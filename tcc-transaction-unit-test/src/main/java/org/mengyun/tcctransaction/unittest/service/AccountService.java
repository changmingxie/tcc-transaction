package org.mengyun.tcctransaction.unittest.service;

import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * Created by changmingxie on 10/25/15.
 */
public interface AccountService {

    void transferTo(TransactionContext transactionContext, long accountId, int amount);

    void transferToConfirm(TransactionContext transactionContext, long accountId, int amount);

    void transferToCancel(TransactionContext transactionContext, long accountId, int amount);

    void transferToWithNoTransactionContext(long accountId, int amount);

    void transferToConfirmWithNoTransactionContext(long accountId, int amount);

    void transferToCancelWithNoTransactionContext(long accountId, int amount);

    void transferFrom(TransactionContext transactionContext, long accountId, int amount);

    void transferFromConfirm(TransactionContext transactionContext, long accountId, int amount);

    void transferFromCancel(TransactionContext transactionContext, long accountId, int amount);

    void transferToWithMultipleTier(TransactionContext transactionContext, long accountId, int amount);

    void transferFromWithMultipleTier(TransactionContext transactionContext, long accountId, int amount);
}
