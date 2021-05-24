package org.mengyun.tcctransaction.unit.test;

import org.junit.Assert;
import org.junit.Test;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.unittest.entity.SubAccount;
import org.mengyun.tcctransaction.unittest.repository.SubAccountRepository;
import org.mengyun.tcctransaction.unittest.service.AccountService;
import org.mengyun.tcctransaction.unittest.service.AccountServiceImpl;
import org.mengyun.tcctransaction.unittest.utils.MessageConstants;
import org.mengyun.tcctransaction.unittest.utils.TraceLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AccountServiceTest extends AbstractTestCase {

    @Autowired
    SubAccountRepository subAccountRepository;

    @Autowired
    private AccountServiceImpl accountService;

    @Test
    public void testTransferFromWithoutTransactionContext() {

        //when
        accountService.transferFrom(null, 1, 50);

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 50);
        Assert.assertTrue(subAccountFrom.getFrozenAmount() == 0);

        List<String> messages = TraceLog.getMessages();

        Assert.assertEquals(2, messages.size());
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CALLED, messages.get(0));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CONFIRM_CALLED, messages.get(1));

    }

    @Test
    public void testTransferToWithoutTransactionContext() {

        //when
        accountService.transferTo(null, 2, 50);

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 150);
        Assert.assertTrue(subAccountFrom.getFrozenAmount() == 0);

        List<String> messages = TraceLog.getMessages();

        Assert.assertEquals(2, messages.size());
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CALLED, messages.get(0));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CONFIRM_CALLED, messages.get(1));
    }

    @Test
    public void testTransferFromWithTransactionContext() {

        //given
        TransactionContext transactionContext = new TransactionContext(new TransactionXid(), TransactionStatus.TRYING.getId());
        //when
        accountService.transferFrom(transactionContext, 1, 50);

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 50);
        Assert.assertTrue(subAccountFrom.getFrozenAmount() == 50);

        List<String> messages = TraceLog.getMessages();

        Assert.assertEquals(1, messages.size());
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CALLED, messages.get(0));
    }

    @Test
    public void testTransferToWithTransactionContext() {

        //given
        TransactionContext transactionContext = new TransactionContext(new TransactionXid(), TransactionStatus.TRYING.getId());
        //when
        accountService.transferTo(transactionContext, 2, 50);

        //then
        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountTo.getBalanceAmount() == 100);
        Assert.assertTrue(subAccountTo.getFrozenAmount() == 0);

        List<String> messages = TraceLog.getMessages();

        Assert.assertEquals(1, messages.size());
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CALLED, messages.get(0));
    }
}
