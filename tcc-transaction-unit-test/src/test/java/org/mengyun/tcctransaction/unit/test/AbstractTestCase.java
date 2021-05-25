package org.mengyun.tcctransaction.unit.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mengyun.tcctransaction.unittest.entity.SubAccount;
import org.mengyun.tcctransaction.unittest.repository.SubAccountRepository;
import org.mengyun.tcctransaction.unittest.utils.TraceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by changmingxie on 12/2/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/tcc-transaction-unit-test.xml", "classpath:/tcc-transaction.xml"})
public abstract class AbstractTestCase {


    @Autowired
    SubAccountRepository subAccountRepository;

    @Before
    public void init() {
        initMethodCallSeq();
        buildAccount();
    }


    private void initMethodCallSeq() {
        TraceLog.clear();
    }


    private void buildAccount() {
        SubAccount subAccountFrom = subAccountRepository.findById(1L);
        subAccountFrom.setBalanceAmount(100);
        subAccountFrom.setFrozenAmount(0);
        subAccountRepository.save(subAccountFrom);

        SubAccount subAccountTo = subAccountRepository.findById(2L);
        subAccountTo.setBalanceAmount(100);
        subAccountTo.setFrozenAmount(0);
        subAccountRepository.save(subAccountTo);
    }

}