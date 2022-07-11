package org.mengyun.tcctransaction.unittest;

import org.junit.*;
import org.mengyun.tcctransaction.ServerConfig;
import org.mengyun.tcctransaction.TccServer;
import org.mengyun.tcctransaction.unittest.utils.MessageConstants;
import org.mengyun.tcctransaction.unittest.utils.TraceLog;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ContextConfiguration(locations = {
        "classpath:/tcc-transaction-ut-with-remoting-storage.xml"})
@Ignore
public class TransferServiceWithRemotingStorageTest extends TransferServiceTest {

    private TccServer tccServer;

    @Before
    public void init() throws Exception {
        super.init();
        this.tccServer = new TccServer(new ServerConfig());
        tccServer.start();
    }

    @After
    public void close() throws Exception {
        if (tccServer != null) {
            tccServer.shutdown();
        }
    }

    @Test
    public void testTransferWithTimeoutAndCancelBeforeBranchTransactionStart() {
        super.testTransferWithTimeoutAndCancelBeforeBranchTransactionStart();

        List<String> messages = TraceLog.getMessages();
        Assert.assertEquals(6, messages.size());
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CALLED, messages.get(4));
        Assert.assertEquals(MessageConstants.ACCOUNT_SERVICE_IMPL_TRANSFER_TO_CANCEL_CALLED, messages.get(5));
    }
}
