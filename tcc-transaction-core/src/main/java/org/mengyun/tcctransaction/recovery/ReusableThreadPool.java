package org.mengyun.tcctransaction.recovery;

import org.quartz.SchedulerConfigException;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nervose.Wu
 * @date 2022/7/8 15:10
 */
public class ReusableThreadPool implements ThreadPool {

    private static final Logger logger = LoggerFactory.getLogger(ReusableThreadPool.class.getSimpleName());
    private static SimpleThreadPool instance;
    private static AtomicInteger inuseCounter = new AtomicInteger(0);
    private int threadCount = -1;
    private int threadPriority = Thread.NORM_PRIORITY;
    private boolean threadsInheritGroupOfInitializingThread = true;
    private boolean threadsInheritContextClassLoaderOfInitializingThread = false;
    private boolean makeThreadsDaemons = false;
    private String threadNamePrefix;

    public ReusableThreadPool() {
    }

    @Override
    public boolean runInThread(Runnable runnable) {
        return instance.runInThread(runnable);
    }

    @Override
    public int blockForAvailableThreads() {
        return instance.blockForAvailableThreads();
    }

    @Override
    public void initialize() throws SchedulerConfigException {
        if (instance == null) {
            synchronized (ReusableThreadPool.class) {
                if (instance == null) {
                    SimpleThreadPool simpleThreadPool = null;
                    try {
                        simpleThreadPool = new SimpleThreadPool();
                        simpleThreadPool.setThreadCount(threadCount);
                        simpleThreadPool.setThreadPriority(threadPriority);
                        simpleThreadPool.setThreadNamePrefix(threadNamePrefix);
                        simpleThreadPool.setThreadsInheritGroupOfInitializingThread(threadsInheritGroupOfInitializingThread);
                        simpleThreadPool.setThreadsInheritContextClassLoaderOfInitializingThread(threadsInheritContextClassLoaderOfInitializingThread);
                        simpleThreadPool.setMakeThreadsDaemons(makeThreadsDaemons);
                        simpleThreadPool.setInstanceName("multi-scheduler");
                        simpleThreadPool.initialize();
                        instance = simpleThreadPool;
                    } catch (Exception e) {
                        if (simpleThreadPool != null) {
                            try {
                                simpleThreadPool.shutdown(false);
                            } catch (Exception ignore) {
                            }
                        }
                        instance = null;
                        throw e;
                    }
                }
            }
        }
        logger.debug("one user joined, current inuse counter:{}", inuseCounter.incrementAndGet());
    }

    @Override
    public void shutdown(boolean waitForJobsToComplete) {
        logger.debug("one user quit, current inuse counter:{}", inuseCounter.decrementAndGet());
    }

    @Override
    public int getPoolSize() {
        return instance.getPoolSize();
    }

    @Override
    public void setInstanceId(String schedInstId) {
    }

    @Override
    public void setInstanceName(String schedName) {
    }

    /**
     * called by quartz
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public void setThreadsInheritGroupOfInitializingThread(boolean threadsInheritGroupOfInitializingThread) {
        this.threadsInheritGroupOfInitializingThread = threadsInheritGroupOfInitializingThread;
    }

    public void setThreadsInheritContextClassLoaderOfInitializingThread(boolean threadsInheritContextClassLoaderOfInitializingThread) {
        this.threadsInheritContextClassLoaderOfInitializingThread = threadsInheritContextClassLoaderOfInitializingThread;
    }

    public void setMakeThreadsDaemons(boolean makeThreadsDaemons) {
        this.makeThreadsDaemons = makeThreadsDaemons;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }
}
