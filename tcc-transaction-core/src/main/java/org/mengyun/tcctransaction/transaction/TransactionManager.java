package org.mengyun.tcctransaction.transaction;

import org.mengyun.tcctransaction.ClientConfig;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.exception.CancellingException;
import org.mengyun.tcctransaction.exception.ConfirmingException;
import org.mengyun.tcctransaction.exception.NoExistedTransactionException;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.remoting.exception.RemotingTimeoutException;
import org.mengyun.tcctransaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by changmingxie on 10/26/15.
 */
public class TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class.getSimpleName());
    private static final ThreadLocal<Deque<Transaction>> CURRENT = new ThreadLocal<>();

    private ExecutorService asyncTerminatorExecutorService;

    private TransactionRepository transactionRepository;


    public TransactionManager(TransactionRepository transactionRepository, ClientConfig clientConfig) {
        this.transactionRepository = transactionRepository;
        this.asyncTerminatorExecutorService = new ThreadPoolExecutor(clientConfig.getAsyncConfirmCancelThreadPoolSize(),
                clientConfig.getAsyncConfirmCancelThreadPoolSize(),
                0L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(clientConfig.getAsyncConfirmCancelThreadPoolQueueSize()), new ThreadPoolExecutor.DiscardPolicy());
    }

    public Transaction begin(Object uniqueIdentify) {
        Transaction transaction = new Transaction(uniqueIdentify, this.transactionRepository.getDomain());
        //for performance tuning, at create stage do not persistent
        if (transaction.getXid().getFormatId() == Xid.CUSTOMIZED) {
            //for customized xid, ensure the transaction is created only once before tcc stage
            try {
                transactionRepository.create(transaction);
            } catch (RemotingTimeoutException exception) {
                try {
                    //Try to avoid transaction stuck at trying status.
                    transactionRepository.delete(transaction);
                } catch (Exception rollbackException) {
                    logger.warn("delete transaction failed", rollbackException);
                }
                throw exception;
            }
        }
        registerTransaction(transaction);
        return transaction;
    }

    public Transaction propagationNewBegin(TransactionContext transactionContext) {

        Transaction transaction = new Transaction(transactionContext);

        //for performance tuning, at create stage do not persistent
//        transactionRepository.create(transaction);
        registerTransaction(transaction);
        return transaction;
    }

    public Transaction propagationExistBegin(TransactionContext transactionContext) throws NoExistedTransactionException {
        Transaction transaction = transactionRepository.findByXid(transactionContext.getXid());

        if (transaction != null) {
            registerTransaction(transaction);
            return transaction;
        } else {
            throw new NoExistedTransactionException();
        }
    }

    public void enlistParticipant(Participant participant) {
        Transaction transaction = this.getCurrentTransaction();
        transaction.enlistParticipant(participant);

        if (transaction.getVersion() == 0L) {
            // transaction.getVersion() is zero which means never persistent before, need call create to persistent.
            transactionRepository.create(transaction);
        } else {
            transactionRepository.update(transaction);
        }
    }

    public void commit(boolean asyncCommit) {

        final Transaction transaction = getCurrentTransaction();

        transaction.setStatus(TransactionStatus.CONFIRMING);

        transactionRepository.update(transaction);

        if (asyncCommit) {
            try {
                long statTime = System.currentTimeMillis();

                asyncTerminatorExecutorService.submit(() -> commitTransaction(transaction));
                logger.debug("async submit cost time:{}", System.currentTimeMillis() - statTime);
            } catch (Throwable commitException) {
                logger.warn("compensable transaction async submit confirm failed, recovery job will try to confirm later.", commitException.getCause());
                //throw new ConfirmingException(commitException);
            }
        } else {
            commitTransaction(transaction);
        }
    }


    public void rollback(boolean asyncRollback, boolean needDelay) {

        final Transaction transaction = getCurrentTransaction();
        transaction.setStatus(TransactionStatus.CANCELLING);

        transactionRepository.update(transaction);

        if (!needDelay) {
            if (asyncRollback) {

                try {
                    asyncTerminatorExecutorService.submit(() -> rollbackTransaction(transaction));
                } catch (Throwable rollbackException) {
                    logger.warn("compensable transaction async rollback failed, recovery job will try to rollback later.", rollbackException);
                    throw new CancellingException(rollbackException);
                }
            } else {

                rollbackTransaction(transaction);
            }
        }
    }


    private void commitTransaction(Transaction transaction) {
        try {
            transaction.commit();
            transactionRepository.delete(transaction);
        } catch (Throwable commitException) {

            //try save updated transaction
            try {
                transactionRepository.update(transaction);
            } catch (Exception e) {
                //ignore any exception here
            }

            logger.warn("compensable transaction confirm failed, recovery job will try to confirm later.", commitException);
            throw new ConfirmingException(commitException);
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        try {
            transaction.rollback();
            transactionRepository.delete(transaction);
        } catch (Throwable rollbackException) {

            //try save updated transaction
            try {
                transactionRepository.update(transaction);
            } catch (Exception e) {
                //ignore any exception here
            }

            logger.warn("compensable transaction rollback failed, recovery job will try to rollback later.", rollbackException);
            throw new CancellingException(rollbackException);
        }
    }

    public Transaction getCurrentTransaction() {
        if (isTransactionActive()) {
            return CURRENT.get().peek();
        }
        return null;
    }

    public boolean isTransactionActive() {
        Deque<Transaction> transactions = CURRENT.get();
        return transactions != null && !transactions.isEmpty();
    }


    private void registerTransaction(Transaction transaction) {

        if (CURRENT.get() == null) {
            CURRENT.set(new LinkedList<Transaction>());
        }

        CURRENT.get().push(transaction);
    }

    public void cleanAfterCompletion(Transaction transaction) {
        if (isTransactionActive() && transaction != null) {
            Transaction currentTransaction = getCurrentTransaction();
            if (currentTransaction == transaction) {
                CURRENT.get().pop();
                if (CURRENT.get().isEmpty()) {
                    CURRENT.remove();
                }
            } else {
                throw new SystemException("Illegal transaction when clean after completion");
            }
        }
    }

    public void changeStatus(TransactionStatus status) {
        Transaction transaction = this.getCurrentTransaction();
        transaction.setStatus(status);
        transactionRepository.update(transaction);
    }
}
