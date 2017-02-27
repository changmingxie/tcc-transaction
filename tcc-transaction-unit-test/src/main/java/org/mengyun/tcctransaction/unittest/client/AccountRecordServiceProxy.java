package org.mengyun.tcctransaction.unittest.client;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.unittest.thirdservice.AccountRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * Created by changmingxie on 12/3/15.
 */
@Service
public class AccountRecordServiceProxy {


    @Autowired
    private AccountRecordService accountRecordService;

    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    public void record(final TransactionContext transactionContext, final long accountId, final int amount) {
//        Future<Boolean> future = this.executorService
//                .submit(new Callable<Boolean>() {
//                    @Override
//                    public Boolean call() throws Exception {
//                        accountRecordService.record(transactionContext, accountId, amount);
//                        return true;
//                    }
//                });
//
//        handleResult(future);

        accountRecordService.record(transactionContext, accountId, amount);

    }

    private void handleResult(Future<Boolean> future) {
        while (!future.isDone()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            future.get();
        } catch (InterruptedException e) {
            throw new Error(e);
        } catch (ExecutionException e) {
            throw new Error(e);
        }
    }
}
