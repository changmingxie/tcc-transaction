package org.mengyun.tcctransaction.unittest.service;

import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.unittest.entity.SubAccount;
import org.mengyun.tcctransaction.unittest.repository.SubAccountRepository;
import org.mengyun.tcctransaction.unittest.utils.MessageConstants;
import org.mengyun.tcctransaction.unittest.utils.TraceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by changmingxie on 10/25/15.
 */
@Service
@Qualifier("accountServiceProvider")
public class AccountServiceImpl implements AccountService {


    @Autowired
    SubAccountRepository subAccountRepository;

    @Override
    @Compensable(confirmMethod = "transferFromConfirm", cancelMethod = "transferFromCancel")
    public void transferFrom(TransactionContext transactionContext, long accountId, int amount) {
        TraceLog.debug(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CALLED);
        SubAccount subAccount = subAccountRepository.findById(accountId);
        subAccount.setBalanceAmount(subAccount.getBalanceAmount() - amount);
        subAccount.setFrozenAmount(subAccount.getFrozenAmount() + amount);
        subAccountRepository.save(subAccount);
    }

    @Override
    @Compensable(propagation = Propagation.REQUIRED, confirmMethod = "transferToConfirm", cancelMethod = "transferToCancel")
    public void transferTo(TransactionContext transactionContext, long accountId, int amount) {
        TraceLog.debug(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CALLED);
    }

    public void transferFromConfirm(TransactionContext transactionContext, long accountId, int amount) {
        TraceLog.debug(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CONFIRM_CALLED);
        SubAccount subAccount = subAccountRepository.findById(accountId);
        subAccount.setFrozenAmount(subAccount.getFrozenAmount() - amount);
        subAccountRepository.save(subAccount);
    }

    public void transferFromCancel(TransactionContext transactionContext, long accountId, int amount) {
        TraceLog.debug(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CANCEL_CALLED);
        SubAccount subAccount = subAccountRepository.findById(accountId);

        subAccount.setBalanceAmount(subAccount.getBalanceAmount() + amount);
        subAccount.setFrozenAmount(subAccount.getFrozenAmount() - amount);
        subAccountRepository.save(subAccount);
    }

    public void transferToConfirm(TransactionContext transactionContext, long accountId, int amount) {
        TraceLog.debug(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CONFIRM_CALLED);
        SubAccount subAccount = subAccountRepository.findById(accountId);
        subAccount.setBalanceAmount(subAccount.getBalanceAmount() + amount);
        subAccountRepository.save(subAccount);
    }

    public void transferToCancel(TransactionContext transactionContext, long accountId, int amount) {
        TraceLog.debug(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CANCEL_CALLED);
    }
}
