package org.mengyun.tcctransaction.unittest;

import org.junit.Assert;
import org.junit.Test;
import org.mengyun.tcctransaction.TccClient;
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

public abstract class TransferServiceTest extends AbstractTestCase {

    @Autowired
    SubAccountRepository subAccountRepository;

    @Autowired
    private TransferService transferService;

    @Autowired
    private TccClient tccClient;

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
    public void testTransferWithTryFailedByTimeout() {
        System.out.println(String.format("testTransferWithTryFailed will cost about %ds， please wait!", tccClient.getClientConfig().getRecoverDuration() * 2));
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

        try {
            Thread.sleep(tccClient.getClientConfig().getRecoverDuration() * 2 * 1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messages = TraceLog.getMessages();

        Assert.assertEquals(6, messages.size());
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CANCEL_CALLED, messages.get(5));

    }

    @Test
    public void testTransferWithTimeoutAndCancelBeforeBranchTransactionStart() {
        System.out.println(String.format("testTransferWithTimeoutToTryAndCancelBeforeBranchTransactionStart will cost about %ds，please wait!", tccClient.getClientConfig().getRecoverDuration() * 2));
        //given

        //when
        try {
            transferService.transferWithTimeoutBeforeBranchTransactionStart(1, 2, 50);
        } catch (Throwable e) {

        }

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 100);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 100);

        List<String> messages = TraceLog.getMessages();
        Assert.assertEquals(4, messages.size());

        Assert.assertEquals(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED, messages.get(0));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CALLED, messages.get(1));
        Assert.assertEquals(MessageConstants.TRANSFER_SERVER_TRANSFER_CANCEL_CALLED, messages.get(2));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CANCEL_CALLED, messages.get(3));

        try {
            Thread.sleep(tccClient.getClientConfig().getRecoverDuration() * 2 * 1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testTransferWithTryFailedByException() {
        System.out.println("testTransferWithTryFailed will cost about 30s， please wait!");
        //given

        //when
        try {
            transferService.transferWithException(1, 2, 50);
        } catch (Throwable e) {

        }

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 100);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 100);

        List<String> messages = TraceLog.getMessages();
        Assert.assertEquals(6, messages.size());

        Assert.assertEquals(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED, messages.get(0));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CALLED, messages.get(1));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CALLED, messages.get(2));

        Assert.assertEquals(MessageConstants.TRANSFER_SERVER_TRANSFER_CANCEL_CALLED, messages.get(3));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_FROM_CANCEL_CALLED, messages.get(4));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CANCEL_CALLED, messages.get(5));
    }
}