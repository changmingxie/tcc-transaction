package org.mengyun.tcctransaction.unittest.client;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.unittest.service.AccountService;
import org.mengyun.tcctransaction.unittest.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class AccountServiceStub implements AccountService {

    @Autowired
    private AccountServiceImpl accountService;

    @Override
    public void transferTo(TransactionContext transactionContext, long accountId, int amount) {

        CompletableFuture future = CompletableFuture.runAsync(() -> accountService.transferTo(transactionContext, accountId, amount));

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void transferToWithTimeout(TransactionContext transactionContext, long accountId, int amount) {

        CompletableFuture future = CompletableFuture.runAsync(() -> accountService.transferToWithTimeout(transactionContext, accountId, amount));

        try {
            future.get(1000l, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void transferToWithException(TransactionContext transactionContext, long accountId, int amount) {

        CompletableFuture future = CompletableFuture.runAsync(() -> accountService.transferToWithException(transactionContext, accountId, amount));

        try {
            future.get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void transferFrom(TransactionContext transactionContext, long accountId, int amount) {

        CompletableFuture future = CompletableFuture.runAsync(() -> accountService.transferFrom(transactionContext, accountId, amount));

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
