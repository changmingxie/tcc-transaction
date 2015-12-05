package org.mengyun.tcctransaction.spring.recover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by changmingxie on 11/10/15.
 */
@Component("transactionRecoveryJob")
public class TransactionRecoveryJob {

    @Autowired
    private TransactionRecovery transactionRecovery;

    @Scheduled(cron = "0 */1 * * * ?")
    public void recover() {
        transactionRecovery.startRecover();
    }
}
