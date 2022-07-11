package org.mengyun.tcctransaction.unittest;


import org.junit.Assert;
import org.junit.Test;
import org.mengyun.tcctransaction.unittest.utils.MessageConstants;
import org.mengyun.tcctransaction.unittest.utils.TraceLog;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ContextConfiguration(locations = {
        "classpath:/tcc-transaction-ut-with-memory-storage.xml"})
public class TransferServiceWithMemoryStorageTest extends TransferServiceTest {

    @Test
    public void testTransferWithTimeoutAndCancelBeforeBranchTransactionStart() {
        super.testTransferWithTimeoutAndCancelBeforeBranchTransactionStart();

        List<String> messages = TraceLog.getMessages();
        Assert.assertEquals(5, messages.size());
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CALLED, messages.get(4));
    }
}
