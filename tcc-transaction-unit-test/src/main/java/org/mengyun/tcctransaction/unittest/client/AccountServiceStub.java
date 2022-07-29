package org.mengyun.tcctransaction.unittest.client;

import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.context.TransactionContextHolder;
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

    @EnableTcc
    @Override
    public void transferTo(TransactionContext transactionContext, long accountId, int amount) {

        TransactionContext transactionContext1 = TransactionContextHolder.getCurrentTransactionContext();

        CompletableFuture future = CompletableFuture.runAsync(() -> {

            TransactionContextHolder.setCurrentTransactionContext(transactionContext1);
            accountService.transferTo(transactionContext, accountId, amount);
        });

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @EnableTcc
    public void transferToWithTimeout(TransactionContext transactionContext, long accountId, int amount) {

        TransactionContext transactionContext1 = TransactionContextHolder.getCurrentTransactionContext();

        CompletableFuture future = CompletableFuture.runAsync(() -> {
            TransactionContextHolder.setCurrentTransactionContext(transactionContext1);
            accountService.transferToWithTimeout(transactionContext, accountId, amount);
        });

        try {
            future.get(1000L, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @EnableTcc
    public void transferToWithTimeoutBeforeBranchTransactionStart(TransactionContext transactionContext, long accountId, int amount) {

        TransactionContext transactionContext1 = TransactionContextHolder.getCurrentTransactionContext();
        CompletableFuture future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {

                TransactionContextHolder.setCurrentTransactionContext(transactionContext1);

                if (transactionContext1.getStatus().equals(TransactionStatus.TRYING)) {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                accountService.transferTo(transactionContext, accountId, amount);
            }
        });

        try {
            future.get(1000L, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @EnableTcc
    @Override
    public void transferToWithException(TransactionContext transactionContext, long accountId, int amount) {

        TransactionContext transactionContext1 = TransactionContextHolder.getCurrentTransactionContext();
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            TransactionContextHolder.setCurrentTransactionContext(transactionContext1);
            accountService.transferToWithException(transactionContext, accountId, amount);
        });

        try {
            future.get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    @EnableTcc
    @Override
    public void transferFrom(TransactionContext transactionContext, long accountId, int amount) {

        TransactionContext transactionContext1 = TransactionContextHolder.getCurrentTransactionContext();
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            TransactionContextHolder.setCurrentTransactionContext(transactionContext1);
            accountService.transferFrom(transactionContext, accountId, amount);
        });

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
