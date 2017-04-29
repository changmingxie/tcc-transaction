package org.mengyun.tcctransaction.unit.test;

import org.junit.Assert;
import org.junit.Test;
import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.recover.TransactionRecovery;
import org.mengyun.tcctransaction.unittest.client.TransferService;
import org.mengyun.tcctransaction.unittest.entity.AccountRecord;
import org.mengyun.tcctransaction.unittest.entity.AccountStatus;
import org.mengyun.tcctransaction.unittest.entity.SubAccount;
import org.mengyun.tcctransaction.unittest.repository.AccountRecordRepository;
import org.mengyun.tcctransaction.unittest.repository.SubAccountRepository;
import org.mengyun.tcctransaction.unittest.utils.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by changmingxie on 12/3/15.
 */
public class TransferServiceTest extends AbstractTestCase {

    @Autowired
    private TransferService transferService;

    @Autowired
    SubAccountRepository subAccountRepository;

    @Autowired
    AccountRecordRepository accountRecordRepository;

    @Autowired
    TransactionRecovery transactionRecovery;

    @Test
    public void testTransfer() throws InterruptedException {

        //given
        buildAccount();

        //when
        transferService.transfer(1, 2, 50);

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getStatus() == AccountStatus.NORMAL.getId());
        Assert.assertTrue(subAccountTo.getStatus() == AccountStatus.NORMAL.getId());

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 50);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 250);
    }

    @Test
    public void testTransferWithMandatoryPropagtion() throws InterruptedException {

        //given
        buildAccount();

        //when
        try {
            transferService.transferWithMandatoryPropagation(1, 2, 50);
        } catch (SystemException e) {
            Assert.assertTrue(e.getMessage().startsWith("no active compensable transaction while propagation is mandatory for method"));
        }
    }

    @Test
    public void testTransferWithMultipleTier() {

        //given
        buildAccount();

        //when
        transferService.transferWithMultipleTier(1, 2, 50);

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getStatus() == AccountStatus.NORMAL.getId());
        Assert.assertTrue(subAccountTo.getStatus() == AccountStatus.NORMAL.getId());

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 50);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 250);


        AccountRecord accountRecordTo = accountRecordRepository.findById(2L);

        Assert.assertTrue(accountRecordTo.getBalanceAmount() == 50);
        Assert.assertTrue(accountRecordTo.getStatusId() == AccountStatus.NORMAL.getId());
    }

    @Test
    public void testTransferWithMultiplerConsumer() {
        //given
        buildAccount();

        //when
        transferService.transferWithMultipleConsumer(1, 2, 70);

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getStatus() == AccountStatus.NORMAL.getId());
        Assert.assertTrue(subAccountTo.getStatus() == AccountStatus.NORMAL.getId());

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 30);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 270);
    }

    @Test
    public void testTransferWithOnlyTryAndMultipleConsumer() {
        //given
        buildAccount();

        //when
        transferService.transferWithOnlyTryAndMultipleConsumer(1, 2, 70);

        //then
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getStatus() == AccountStatus.NORMAL.getId());
        Assert.assertTrue(subAccountTo.getStatus() == AccountStatus.NORMAL.getId());

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 30);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 270);
    }

    @Test
    public void testTransferWithNoTransactionContext() {
        //given
        buildAccount();

        //when
        transferService.transferWithNoTransactionContext(1, 2, 70);

        //then
        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountTo.getStatus() == AccountStatus.NORMAL.getId());
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 270);
    }


    @Test
    public void testTryingRecovery() {

        //given
        UnitTest.TRYING_EXCEPTION = true;

        try {
            //given
            buildAccount();

            //when
            transferService.transferWithMultipleConsumer(1, 2, 70);

        } catch (Throwable e) {

        }

        System.out.println("begin recovery");

        //when
        UnitTest.TRYING_EXCEPTION = false;

        //then
        AccountRecord accountRecord = accountRecordRepository.findById(1L);
        Assert.assertTrue(accountRecord.getBalanceAmount() == 70);

        try {
            //waiting the auto recovery schedule
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        Assert.assertTrue(accountRecord.getBalanceAmount() == 0);

    }

    @Test
    public void testConfirmingRecovery() {

        //given
        UnitTest.CONFIRMING_EXCEPTION = true;

        try {
            //given
            buildAccount();

            //when
            transferService.transferWithMultipleConsumer(1, 2, 70);

        } catch (Throwable e) {

        }

        System.out.println("begin recovery");

        //when
        UnitTest.CONFIRMING_EXCEPTION = false;

        //then
        AccountRecord accountRecord = accountRecordRepository.findById(1L);
        Assert.assertTrue(accountRecord.getBalanceAmount() == 70);

        try {
            //waiting the auto recovery schedule
            Thread.sleep(1000 * 10L);
        } catch (InterruptedException e) {
            throw new Error(e);
        }

        Assert.assertTrue(accountRecord.getBalanceAmount() == 70);

        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        SubAccount subAccountTo = subAccountRepository.findById(2L);

        Assert.assertTrue(subAccountFrom.getStatus() == AccountStatus.NORMAL.getId());
        Assert.assertTrue(subAccountTo.getStatus() == AccountStatus.NORMAL.getId());

        Assert.assertTrue(subAccountFrom.getBalanceAmount() == 30);
        Assert.assertTrue(subAccountTo.getBalanceAmount() == 270);


    }


    private void buildAccount() {
        SubAccount subAccountFrom = subAccountRepository.findById(1L);

        subAccountFrom.setBalanceAmount(100);
        subAccountFrom.setStatus(AccountStatus.NORMAL.getId());

        SubAccount subAccountTo = subAccountRepository.findById(2L);
        subAccountTo.setBalanceAmount(200);
        subAccountTo.setStatus(AccountStatus.NORMAL.getId());

        AccountRecord accountRecordFrom = accountRecordRepository.findById(1L);
        accountRecordFrom.setBalanceAmount(0);
        accountRecordFrom.setStatusId(AccountStatus.NORMAL.getId());

        AccountRecord accountRecordTo = accountRecordRepository.findById(2L);
        accountRecordTo.setBalanceAmount(0);
        accountRecordTo.setStatusId(AccountStatus.NORMAL.getId());
    }
}