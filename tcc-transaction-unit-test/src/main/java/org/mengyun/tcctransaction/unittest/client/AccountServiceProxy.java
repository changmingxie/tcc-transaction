package org.mengyun.tcctransaction.unittest.client;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.unittest.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * Created by changmingxie on 12/3/15.
 */
@Service
public class AccountServiceProxy {

    @Autowired
    AccountServiceImpl accountService;

    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    public void transferFromWithMultipleTier(final TransactionContext transactionContext, final long accountId, final int amount) {
        Future<Boolean> future = this.executorService
                .submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        accountService.transferFromWithMultipleTier(transactionContext, accountId, amount);
                        return true;
                    }
                });

        handleResult(future);
    }

    public void transferToWithMultipleTier(final TransactionContext transactionContext, final long accountId, final int amount) {
        Future<Boolean> future = this.executorService
                .submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        accountService.transferToWithMultipleTier(transactionContext, accountId, amount);
                        return true;
                    }
                });

        handleResult(future);
    }


    public void transferTo(final TransactionContext transactionContext, final long accountId, final int amount) {

        Future<Boolean> future = this.executorService
                .submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        accountService.transferTo(transactionContext, accountId, amount);
                        return true;
                    }
                });

        handleResult(future);
    }

    public void transferTo(final long accountId, final int amount) {

        Future<Boolean> future = this.executorService
                .submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        accountService.transferTo(accountId, amount);
                        return true;
                    }
                });

        handleResult(future);
    }

    public void transferFrom(final TransactionContext transactionContext, final long accountId, final int amount) {

        Future<Boolean> future = this.executorService
                .submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        accountService.transferFrom(transactionContext, accountId, amount);
                        return true;
                    }
                });

        handleResult(future);
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
