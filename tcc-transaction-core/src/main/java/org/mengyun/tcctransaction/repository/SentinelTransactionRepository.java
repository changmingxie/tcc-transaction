package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.ha.SentinelController;

import javax.transaction.xa.Xid;
import java.util.Date;

public class SentinelTransactionRepository extends CacheableTransactionRepository {

    private SentinelController sentinelController;

    private CacheableTransactionRepository workTransactionRepository;

    private CacheableTransactionRepository degradedTransactionRepository;

    public SentinelTransactionRepository() {
    }

    public void init() {

        if (workTransactionRepository == null) {
            throw new SystemException("workTransactionRepository cann't be null");
        }

        if (degradedTransactionRepository == null) {
            throw new SystemException("degradedTransactionRepository cann't be null");
        }

        if (sentinelController == null) {
            throw new SystemException("SentinelController cann't be null");
        }

        //disable cache
        workTransactionRepository.setExpireDuration(0);
        degradedTransactionRepository.setExpireDuration(0);
    }

    @Override
    protected int doCreate(Transaction transaction) {

        if (!sentinelController.degrade()) {
            return workTransactionRepository.doCreate(transaction);
        } else {
            return degradedTransactionRepository.doCreate(transaction);
        }
    }

    @Override
    protected int doUpdate(Transaction transaction) {

        if (!sentinelController.degrade()) {
            return workTransactionRepository.doUpdate(transaction);
        } else {
            return degradedTransactionRepository.doUpdate(transaction);
        }
    }

    @Override
    protected int doDelete(Transaction transaction) {

        if (!sentinelController.degrade()) {
            return workTransactionRepository.doDelete(transaction);
        } else {
            return degradedTransactionRepository.doDelete(transaction);
        }
    }

    @Override
    protected Transaction doFindOne(Xid xid) {
        if (!sentinelController.degrade()) {
            return workTransactionRepository.doFindOne(xid);
        } else {
            return degradedTransactionRepository.doFindOne(xid);
        }
    }

    @Override
    protected Page<Transaction> doFindAllUnmodifiedSince(Date date, String offset, int pageSize) {

        if (!sentinelController.degrade()) {
            return workTransactionRepository.doFindAllUnmodifiedSince(date, offset, pageSize);
        } else {
            return degradedTransactionRepository.doFindAllUnmodifiedSince(date, offset, pageSize);
        }
    }

    public SentinelController getSentinelController() {
        return sentinelController;
    }

    public void setSentinelController(SentinelController sentinelController) {
        this.sentinelController = sentinelController;
    }

    public CacheableTransactionRepository getWorkTransactionRepository() {
        return workTransactionRepository;
    }

    public void setWorkTransactionRepository(CacheableTransactionRepository workTransactionRepository) {
        this.workTransactionRepository = workTransactionRepository;
    }

    public CacheableTransactionRepository getDegradedTransactionRepository() {
        return degradedTransactionRepository;
    }

    public void setDegradedTransactionRepository(CacheableTransactionRepository degradedTransactionRepository) {
        this.degradedTransactionRepository = degradedTransactionRepository;
    }

    @Override
    public String getDomain() {
        return null;
    }
}
