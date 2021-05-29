package org.mengyun.tcctransaction.unittest.client;

import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.unittest.utils.MessageConstants;
import org.mengyun.tcctransaction.unittest.utils.TraceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by changmingxie on 10/25/15.
 */
@Service
public class TransferService {


    @Autowired
    AccountServiceProxy accountServiceProxy;

    public TransferService() {
    }

    @Compensable(confirmMethod = "transferConfirm", cancelMethod = "transferCancel")
    @Transactional
    public void transfer(long fromAccountId, long toAccountId, int amount) {

        TraceLog.debug(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED);
        accountServiceProxy.transferFrom(fromAccountId, amount);
        accountServiceProxy.transferTo(toAccountId, amount);
    }

    @Compensable(confirmMethod = "transferConfirm", cancelMethod = "transferCancel")
    @Transactional
    public void transferWithTimeout(long fromAccountId, long toAccountId, int amount) {

        TraceLog.debug(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED);
        accountServiceProxy.transferFrom(fromAccountId, amount);
        accountServiceProxy.transferToWithTimeout(toAccountId, amount);
    }

    @Compensable(confirmMethod = "transferConfirm", cancelMethod = "transferCancel")
    @Transactional
    public void transferWithTimeoutBeforeBranchTransactionStart(long fromAccountId, long toAccountId, int amount) {

        TraceLog.debug(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED);
        accountServiceProxy.transferFrom(fromAccountId, amount);
        accountServiceProxy.transferToWithTimeoutBeforeBranchTransactionStart(toAccountId, amount);
    }

    @Compensable(confirmMethod = "transferConfirm", cancelMethod = "transferCancel")
    @Transactional
    public void transferWithException(long fromAccountId, long toAccountId, int amount) {

        TraceLog.debug(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED);
        accountServiceProxy.transferFrom(fromAccountId, amount);
        accountServiceProxy.transferToWithException(toAccountId, amount);
    }

//    @Compensable(confirmMethod = "transferConfirm", cancelMethod = "transferCancel")
//    @Transactional
//    public void transferWithEmbeddedParticipants(long fromAccountId, long toAccountId, int amount) {
//
//        TraceLog.debug(MessageConstants.TRANSFER_SERVER_TRANSFER_CALLED);
//        accountServiceProxy.transferFrom(null, fromAccountId, amount);
//        accountServiceProxy.transferTo(null, toAccountId, amount);
//    }

    public void transferConfirm(long fromAccountId, long toAccountId, int amount) {
        TraceLog.debug(MessageConstants.TRANSFER_SERVER_TRANSFER_CONFIRM_CALLED);
    }

    public void transferCancel(long fromAccountId, long toAccountId, int amount) {
        TraceLog.debug(MessageConstants.TRANSFER_SERVER_TRANSFER_CANCEL_CALLED);
    }
}
