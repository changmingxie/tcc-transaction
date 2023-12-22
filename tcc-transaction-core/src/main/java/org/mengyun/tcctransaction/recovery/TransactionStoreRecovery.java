package org.mengyun.tcctransaction.recovery;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mengyun.tcctransaction.alert.AlertManager;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.repository.StorageMode;
import org.mengyun.tcctransaction.storage.*;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by changmingxie on 11/10/15.
 */
public class TransactionStoreRecovery implements Closeable {

    public static final int CONCURRENT_RECOVERY_TIMEOUT = 60;

    public static final int MAX_ERROR_COUNT_SHREDHOLD = 15;

    private static final Logger logger = LoggerFactory.getLogger(TransactionStoreRecovery.class.getSimpleName());
    private final AtomicInteger triggerMaxRetryPrintCount = new AtomicInteger();
    private final AtomicInteger recoveryFailedPrintCount = new AtomicInteger();
    private final Lock logSync = new ReentrantLock();
    private volatile int logMaxPrintCount;
    private TransactionStorage transactionStorage;

    private RecoveryExecutor recoveryExecutor;

    private RecoveryConfig recoveryConfig;

    private ExecutorService recoveryExecutorService;

    private StorageMode storageMode = StorageMode.ALONE;

    private ObjectMapper jackson = new ObjectMapper();

    private ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    public TransactionStoreRecovery(TransactionStorage transactionStorage, RecoveryExecutor recoveryExecutor, RecoveryConfig recoveryConfig) {
        this.transactionStorage = transactionStorage;
        this.recoveryExecutor = recoveryExecutor;
        this.recoveryConfig = recoveryConfig;

        recoveryExecutorService = Executors.newFixedThreadPool(recoveryConfig.getConcurrentRecoveryThreadCount());

        logMaxPrintCount = Math.min(recoveryConfig.getFetchPageSize() / 2, MAX_ERROR_COUNT_SHREDHOLD);
    }

    public StorageMode getStoreMode() {
        return storageMode;
    }

    public void setStoreMode(StorageMode storageMode) {
        this.storageMode = storageMode;
    }

    @Override
    public void close() {
        if (recoveryExecutorService != null) {
            recoveryExecutorService.shutdown();
        }
    }

    public void startRecover(String domain) {

        DomainStore domainStore = ((StorageRecoverable) transactionStorage).findDomain(domain);
        rateLimiterMap.compute(domain, (key, old) -> {
                    if (old == null) {
                        return RateLimiter.create(domainStore.getMaxRecoveryRequestPerSecond());
                    } else {
                        old.setRate(domainStore.getMaxRecoveryRequestPerSecond());
                        return old;
                    }
                }
        );

        try {
            String offset = null;

            int totalCount = 0;
            int reachRetryLimitCount=0;
            do {

                Page<TransactionStore> page = loadErrorTransactionsByPage(domain, offset);

                if (!page.getData().isEmpty()) {
                    concurrentRecoveryErrorTransactions(page.getData(),domainStore.getMaxRetryCount());
                    offset = page.getNextOffset();
                    totalCount += page.getData().size();
                    reachRetryLimitCount += page.getData().stream().filter(each -> each.getRetriedCount() > domainStore.getMaxRetryCount()).count();
                } else {
                    break;
                }
            } while (true);

            // 告警
            AlertManager.tryAlert(domain, reachRetryLimitCount, transactionStorage);

            logger.debug("total recovery count {} from repository:{}", totalCount, transactionStorage.getClass().getName());
        } catch (Throwable e) {
            logger.error("recovery failed from repository:{}.", transactionStorage.getClass().getName(), e);
        }
    }

    private Page<TransactionStore> loadErrorTransactionsByPage(String domain, String offset) {

        long currentTimeInMillis = Instant.now().toEpochMilli();

        return ((StorageRecoverable) transactionStorage).findAllUnmodifiedSince(domain, new Date(currentTimeInMillis - recoveryConfig.getRecoverDuration() * 1000L), offset, recoveryConfig.getFetchPageSize());
    }


    private void concurrentRecoveryErrorTransactions(List<TransactionStore> transactions, int maxRetryCount) throws InterruptedException, ExecutionException {

        initLogStatistics();

        List<RecoverTask> tasks = new ArrayList<>();
        for (TransactionStore transaction : transactions) {
            tasks.add(new RecoverTask(transaction, maxRetryCount));
        }

        long timeout = (long) (CONCURRENT_RECOVERY_TIMEOUT + tasks.size() / rateLimiterMap.get(transactions.get(0).getDomain()).getRate());
        List<Future<Void>> futures = recoveryExecutorService.invokeAll(tasks, timeout, TimeUnit.SECONDS);

        for (Future<Void> future : futures) {
            future.get();
        }
    }

    private void recoverErrorTransaction(TransactionStore transactionStore, int maxRetryCount) {

        if (transactionStore.getRetriedCount() > maxRetryCount) {

            logSync.lock();
            try {
                if (triggerMaxRetryPrintCount.get() < logMaxPrintCount) {
                    logger.warn(
                            "recover failed with max retry count,will not try again. domain:{}, xid:{}, rootDomain:{}, rootXid:{}, status:{},retried count:{}",
                            transactionStore.getDomain(),
                            transactionStore.getXid(),
                            transactionStore.getRootDomain(),
                            transactionStore.getRootXid(),
                            transactionStore.getStatusId(),
                            transactionStore.getRetriedCount());
                    triggerMaxRetryPrintCount.incrementAndGet();
                } else if (triggerMaxRetryPrintCount.get() == logMaxPrintCount) {
                    logger.warn("Too many transactionStore's retried count max then MaxRetryCount during one page transactions recover process , will not print errors again!");
                }

            } finally {
                logSync.unlock();
            }

            return;
        }

        try {

            if (transactionStore.getTransactionTypeId() == TransactionType.ROOT.getId()) {

                switch (TransactionStatus.valueOf(transactionStore.getStatusId())) {
                    case CONFIRMING:
                        commitTransaction(transactionStore);
                        break;
                    case CANCELLING:
                        rollbackTransaction(transactionStore);
                        break;
                    case TRYING:
                        tryTreatAsFailed(transactionStore, TransactionStatus.CANCELLING);
                        break;
                    default:
                        //the transactionStore status is TRYING, ignore it.
                        break;

                }

            } else {

                //transactionStore type is BRANCH
                switch (TransactionStatus.valueOf(transactionStore.getStatusId())) {
                    case CONFIRMING:
                        commitTransaction(transactionStore);
                        break;
                    case CANCELLING:
                    case TRY_FAILED:
                        rollbackTransaction(transactionStore);
                        break;
                    case TRY_SUCCESS:

                        if (storageMode == StorageMode.CENTRAL) {

                            //check the root transactionStore
                            TransactionStore rootTransaction = transactionStorage.findByXid(transactionStore.getRootDomain(), transactionStore.getRootXid());

                            if (rootTransaction == null) {
                                // In this case means the root transactionStore is already rollback.
                                // Need cancel this branch transactionStore.
                                rollbackTransaction(transactionStore);
                            } else {
                                switch (TransactionStatus.valueOf(rootTransaction.getStatusId())) {
                                    case CONFIRMING:
                                        commitTransaction(transactionStore);
                                        break;
                                    case CANCELLING:
                                        rollbackTransaction(transactionStore);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        break;
                    case TRYING:
                        tryTreatAsFailed(transactionStore, TransactionStatus.TRY_FAILED);
                        break;
                    default:
                        //ignore it.
                        break;
                }

            }

        } catch (Throwable throwable) {

            if (throwable instanceof TransactionOptimisticLockException
                    || ExceptionUtils.getRootCause(throwable) instanceof TransactionOptimisticLockException) {

                logger.warn(
                        "optimisticLockException happened while recover. txid:{}, status:{},retried count:{}",
                        transactionStore.getXid(),
                        transactionStore.getStatusId(),
                        transactionStore.getRetriedCount());
            } else {

                logSync.lock();
                try {
                    if (recoveryFailedPrintCount.get() < logMaxPrintCount) {
                        try {
                            logger.warn("recover failed, txid:{}, status:{},retried count:{},transactionStore content:{}",
                                    transactionStore.getXid(),
                                    transactionStore.getStatusId(),
                                    transactionStore.getRetriedCount(),
                                    jackson.writeValueAsString(transactionStore), throwable);
                        } catch (JsonProcessingException e) {
                            logger.error("failed to serialize transactionStore {}", transactionStore.toString(), e);
                        }
                        recoveryFailedPrintCount.incrementAndGet();
                    } else if (recoveryFailedPrintCount.get() == logMaxPrintCount) {
                        logger.warn("Too many transactionStore's recover error during one page transactions recover process , will not print errors again!");
                    }
                } finally {
                    logSync.unlock();
                }
            }
        }
    }

    private void tryTreatAsFailed(TransactionStore transactionStore, TransactionStatus transactionStatus) {
        Date lastUpdateTime = transactionStore.getLastUpdateTime();
        Date currentTime = new Date();
        int maxTimeTreatTryingAsFailed = recoveryConfig.getMaxTimeTreatTryingAsFailed();
        if (maxTimeTreatTryingAsFailed > 0
                && (currentTime.getTime() - lastUpdateTime.getTime()) > maxTimeTreatTryingAsFailed * 1000L) {
            //update the status to cancel or try failed, waiting for the recovery task to recover
            transactionStore.setVersion(transactionStore.getVersion() + 1);
            transactionStore.setStatusId(transactionStatus.getId());
            transactionStorage.update(transactionStore);
        }
    }

    private void rollbackTransaction(TransactionStore transactionStore) {
        rateLimiterMap.get(transactionStore.getDomain()).acquire();
        recoveryExecutor.rollback(transactionStore);
    }

    private void commitTransaction(TransactionStore transactionStore) {
        rateLimiterMap.get(transactionStore.getDomain()).acquire();
        recoveryExecutor.commit(transactionStore);
    }

    private void initLogStatistics() {
        triggerMaxRetryPrintCount.set(0);
        recoveryFailedPrintCount.set(0);
    }

    public TransactionStorage getTransactionStorage() {
        return transactionStorage;
    }

    class RecoverTask implements Callable<Void> {

        private TransactionStore transaction;

        private int maxRetryCount;

        public RecoverTask(TransactionStore transaction, int maxRetryCount) {
            this.transaction = transaction;
            this.maxRetryCount = maxRetryCount;
        }

        @Override
        public Void call() throws Exception {
            recoverErrorTransaction(transaction, maxRetryCount);
            return null;
        }
    }
}
