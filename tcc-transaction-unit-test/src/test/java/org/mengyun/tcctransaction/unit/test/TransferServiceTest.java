package org.mengyun.tcctransaction.unit.test;

import org.junit.Assert;
import org.junit.Test;
import org.mengyun.tcctransaction.unittest.client.TransferService;
import org.mengyun.tcctransaction.unittest.entity.SubAccount;
import org.mengyun.tcctransaction.unittest.repository.SubAccountRepository;
import org.mengyun.tcctransaction.unittest.utils.MessageConstants;
import org.mengyun.tcctransaction.unittest.utils.TraceLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by changmingxie on 12/3/15.
 */
public class TransferServiceTest extends AbstractTestCase {

    @Autowired
    SubAccountRepository subAccountRepository;

    @Autowired
    private TransferService transferService;

    @Test
    public void testTransfer() {

        //given

        //when
        transferService.transfer(1, 2, 50);

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 50);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 150);

        List<String> messages = TraceLog.getMessages();
        Assert.assertEquals(6, messages.size());

        Assert.assertEquals(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED, messages.get(0));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CALLED, messages.get(1));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CALLED, messages.get(2));
        Assert.assertEquals(MessageConstants.TRANSFER_SERVER_TRANSFER_CONFIRM_CALLED, messages.get(3));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CONFIRM_CALLED, messages.get(4));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CONFIRM_CALLED, messages.get(5));
    }

    @Test
    public void testTransferWithTryFailed() {

        //given

        //when
        try {
            transferService.transferWithTimeout(1, 2, 50);
        } catch (Throwable e) {

        }

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 100);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 100);

        List<String> messages = TraceLog.getMessages();
        Assert.assertEquals(5, messages.size());

        Assert.assertEquals(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED, messages.get(0));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CALLED, messages.get(1));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CALLED, messages.get(2));

        Assert.assertEquals(MessageConstants.TRANSFER_SERVER_TRANSFER_CANCEL_CALLED, messages.get(3));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CANCEL_CALLED, messages.get(4));
//        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CONFIRM_CALLED, messages.get(5));
    }
}