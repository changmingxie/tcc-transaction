package org.mengyun.tcctransaction.unittest.thirdservice;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.unittest.entity.AccountRecord;
import org.mengyun.tcctransaction.unittest.entity.AccountStatus;
import org.mengyun.tcctransaction.unittest.repository.AccountRecordRepository;
import org.mengyun.tcctransaction.unittest.utils.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by changmingxie on 10/25/15.
 */
@Service
public class AccountRecordServiceImpl implements AccountRecordService {

    @Autowired
    AccountRecordRepository accountRecordRepository;

    @Compensable(confirmMethod = "recordConfirm", cancelMethod = "recordCancel")
    public void record(TransactionContext transactionContext, long accountId, int amount) {

        System.out.println("record");

        AccountRecord accountRecord = accountRecordRepository.findById(accountId);
        accountRecord.setBalanceAmount(amount);
        accountRecord.setStatusId(AccountStatus.TRANSFERING.getId());

        if (UnitTest.TRYING_EXCEPTION) {
            throw new RuntimeException("record try failed.");
        }
    }

    public void recordConfirm(TransactionContext transactionContext, long accountId, int amount) {
        System.out.println("recordConfirm");
        AccountRecord accountRecord = accountRecordRepository.findById(accountId);
        accountRecord.setStatusId(AccountStatus.NORMAL.getId());
    }

    public void recordCancel(TransactionContext transactionContext, long accountId, int amount) {
        System.out.println("recordCancel");

        if (UnitTest.TRYING_EXCEPTION) {
            throw new RuntimeException("record cancel failed.");
        }

        AccountRecord accountRecord = accountRecordRepository.findById(accountId);
        accountRecord.setBalanceAmount(accountRecord.getBalanceAmount() - amount);
        accountRecord.setStatusId(AccountStatus.NORMAL.getId());


    }
}
