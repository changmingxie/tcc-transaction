package org.mengyun.tcctransaction.unittest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by changmingxie on 12/3/15.
 */
@Service
public class AccountServiceProxy {

    @Autowired
    AccountServiceStub accountService;

    public void transferFrom(final long accountId, final int amount) {
        accountService.transferFrom(null, accountId, amount);
    }

    public void transferTo(final long accountId, final int amount) {
        accountService.transferTo(null, accountId, amount);
    }

    public void transferToWithTimeout(final long accountId, final int amount) {
        accountService.transferToWithTimeout(null, accountId, amount);
    }

    public void transferToWithTimeoutBeforeBranchTransactionStart(final long accountId, final int amount) {
        accountService.transferToWithTimeoutBeforeBranchTransactionStart(null, accountId, amount);
    }

    public void transferToWithException(final long accountId, final int amount) {
        accountService.transferToWithException(null, accountId, amount);
    }
}
