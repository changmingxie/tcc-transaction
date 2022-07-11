package org.mengyun.tcctransaction.unittest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mengyun.tcctransaction.unittest.entity.SubAccount;
import org.mengyun.tcctransaction.unittest.repository.SubAccountRepository;
import org.mengyun.tcctransaction.unittest.utils.TraceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by changmingxie on 12/2/15.
 */
@RunWith(SpringRunner.class)
//@DirtiesContext
public abstract class AbstractTestCase {


    @Autowired
    SubAccountRepository subAccountRepository;

    @Before
    public void init() throws Exception {
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