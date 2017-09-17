package org.mengyun.tcctransaction.unit.test;

import org.junit.Test;
import org.mengyun.tcctransaction.Participant;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.serializer.KryoTransactionSerializer;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;
import org.mengyun.tcctransaction.unittest.client.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

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

        ObjectSerializer objectSerializer = new KryoTransactionSerializer();

        Transaction transaction = new Transaction(TransactionType.ROOT);

        transaction.getAttachments().put("abc", new Participant());

        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            byte[] bytes = objectSerializer.serialize(transaction);
            Transaction transaction1 = (Transaction) objectSerializer.deserialize(bytes);

            if (transaction.getVersion() != transaction1.getVersion()) {
                throw new Error();
            }
        }
        long thenTime = System.currentTimeMillis();

        System.out.println(thenTime - currentTime);
    }
}
