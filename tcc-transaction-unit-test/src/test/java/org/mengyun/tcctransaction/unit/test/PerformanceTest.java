package org.mengyun.tcctransaction.unit.test;

import org.junit.Test;
import org.mengyun.tcctransaction.Participant;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.serializer.KryoPoolSerializer;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;
import org.mengyun.tcctransaction.unittest.client.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by changming.xie on 2/24/16.
 */
public class PerformanceTest extends AbstractTestCase {

    @Autowired
    private TransferService transferService;

    @Test
    public void performanceTest() {

        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            transferService.performenceTuningTransfer();
        }

        long thenTime = System.currentTimeMillis();

        System.out.println(thenTime - currentTime);
    }

    @Test
    public void serializeTest() {

        ObjectSerializer objectSerializer = new KryoPoolSerializer();

        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
//
            Transaction transaction = new Transaction(TransactionType.ROOT);
            transaction.getAttachments().put("abc", new Participant());
            byte[] bytes = objectSerializer.serialize(transaction);
            Transaction transaction1 = (Transaction) objectSerializer.deserialize(bytes);

            if (transaction.getVersion() != transaction1.getVersion()) {
                throw new Error();
            }
        }
        long thenTime = System.currentTimeMillis();

        System.out.println(thenTime - currentTime);
    }

    @Test
    public void testThreadPool() throws ExecutionException, InterruptedException {

        ExecutorService executorService = new ThreadPoolExecutor(1, 2,
                30L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>());

        Long startTime = System.currentTimeMillis();

        List<Future> futures = new ArrayList<Future>();

        for (int i = 0; i <= 1; i++) {
            futures.add(executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());

                    System.out.println(Thread.currentThread().getName()+" done");
                }
            }));
        }

        for (Future future : futures) {
            future.get();
        }

        System.out.println("cost time:" + (System.currentTimeMillis() - startTime));

    }
}
