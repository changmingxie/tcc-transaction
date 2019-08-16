package org.mengyun.tcctransaction.unit.test;

import org.junit.Test;
import org.mengyun.tcctransaction.unittest.client.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.UUID;

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
}
