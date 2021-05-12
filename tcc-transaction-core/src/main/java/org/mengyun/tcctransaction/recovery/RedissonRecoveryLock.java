package org.mengyun.tcctransaction.recovery;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedissonRecoveryLock implements RecoveryLock {

    private static final String DEFAULT_LOCK_NAME_PREFIX = "TCC_RECOVERY_LOCK";
    private RedissonClient redissonClient;
    private String lockSuffixName;
    private volatile Lock lock = new ReentrantLock();

    public RedissonRecoveryLock() {
    }

    private void init() {

    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }


    public void setLockSuffixName(String lockSuffixName) {
        this.lockSuffixName = lockSuffixName;
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        if (this.redissonClient != null) {
            lock = redissonClient.getLock(StringUtils.isNotEmpty(lockSuffixName) ? DEFAULT_LOCK_NAME_PREFIX + ":" + lockSuffixName : DEFAULT_LOCK_NAME_PREFIX);
        }
    }
}
