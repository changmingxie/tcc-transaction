package org.mengyun.tcctransaction.unit.test;

import org.junit.Assert;
import org.junit.Test;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.serializer.JacksonJsonSerializer;
import org.mengyun.tcctransaction.unittest.client.TransferService;
import org.mengyun.tcctransaction.unittest.entity.SubAccount;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * Created by changming.xie on 04/04/19.
 */
public class ReflectionTest extends AbstractTestCase {

    @Autowired
    private TransferService transferService;

    @Test
    public void test1() throws NoSuchMethodException {

        transferService.performenceTuningTransfer();

        Method transferMethod = TransferService.class.getMethod("transfer", long.class, long.class, int.class);


    }

    @Test
    public void testJacksonSerializer() {

        String json = "[\"org.mengyun.tcctransaction.Transaction\",{\"xid\":[\"org.mengyun.tcctransaction.api.TransactionXid\",{\"formatId\":1,\"globalTransactionId\":\"TQ5/1W2USTWZHjwV7JajxA==\",\"branchQualifier\":\"Kcjo0GaHTc6hIPpf3B7ARA==\"}],\"status\":\"TRYING\",\"transactionType\":\"ROOT\",\"retriedCount\":0,\"createTime\":[\"java.util.Date\",1575457808693],\"lastUpdateTime\":[\"java.util.Date\",1575457822864],\"version\":2,\"participants\":[\"java.util.ArrayList\",[[\"org.mengyun.tcctransaction.Participant\",{\"xid\":[\"org.mengyun.tcctransaction.api.TransactionXid\",{\"formatId\":1,\"globalTransactionId\":\"TQ5/1W2USTWZHjwV7JajxA==\",\"branchQualifier\":\"H1KzapA5SL+L2mu3D5tyRw==\"}],\"confirmInvocationContext\":[\"org.mengyun.tcctransaction.InvocationContext\",{\"targetClass\":\"org.mengyun.tcctransaction.unittest.client.TransferService\",\"methodName\":\"transferConfirm\",\"parameterTypes\":[\"long\",\"long\",\"int\"],\"args\":[\"[Ljava.lang.Object;\",[[\"java.lang.Long\",1],[\"java.lang.Long\",2],50]]}],\"cancelInvocationContext\":[\"org.mengyun.tcctransaction.InvocationContext\",{\"targetClass\":\"org.mengyun.tcctransaction.unittest.client.TransferService\",\"methodName\":\"transferCancel\",\"parameterTypes\":[\"long\",\"long\",\"int\"],\"args\":[\"[Ljava.lang.Object;\",[[\"java.lang.Long\",1],[\"java.lang.Long\",2],50]]}]}]]],\"attachments\":[\"java.util.concurrent.ConcurrentHashMap\",{}]}]";

        JacksonJsonSerializer jacksonJsonSerializer = new JacksonJsonSerializer();

        Transaction transaction = jacksonJsonSerializer.deserialize(json.getBytes());

        SubAccount subAccount = new SubAccount(1l, 10);

        byte[] bytes = jacksonJsonSerializer.serialize(transaction);

        json = new String(bytes);

        transaction = jacksonJsonSerializer.deserialize(bytes);

        Assert.assertTrue(transaction != null);
    }

}
