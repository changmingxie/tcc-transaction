package org.mengyun.tcctransaction.server.service;

import org.mengyun.tcctransaction.TccServer;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.dto.TransactionDetailRequestDto;
import org.mengyun.tcctransaction.dashboard.dto.TransactionStoreDto;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.service.impl.BaseTransactionServiceImpl;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.xid.TransactionXid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author huabao.fang
 * @Date 2022/5/24 23:17
 **/
@Service
public class TransactionServiceImpl extends BaseTransactionServiceImpl {

    @Autowired
    private TccServer tccServer;

    @Override
    public TransactionStorage getTransactionStorage() {
        return tccServer.getTransactionStorage();
    }

    @Override
    public ResponseDto<TransactionStoreDto> detail(TransactionDetailRequestDto requestDto) {
        String domain = requestDto.getDomain();
        Xid xid = new TransactionXid(requestDto.getXidString());
        TransactionStore transactionStore = getTransactionStorage().findByXid(domain, xid);
        if (transactionStore == null) {
            transactionStore = getTransactionStorage().findMarkDeletedByXid(domain, xid);
        }
        if (transactionStore == null) {
            return ResponseDto.returnFail(ResponseCodeEnum.TRANSACTION_DETAIL_NOT_EXIST);
        }
        TransactionStoreDto transactionStoreDto = toTransactionStoreDto(transactionStore);
        if (!isJSONString(transactionStoreDto.getContent())) {
            byte[] visualizedContent = null;
            try {
                visualizedContent = tccServer.getRecoveryExecutor().transactionVisualize(domain, transactionStore.getContent());
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                if (errorMessage != null && errorMessage.length() > 100) {
                    errorMessage = errorMessage.substring(0, 100).concat("...");
                }
                return ResponseDto.returnFail(ResponseCodeEnum.TRANSACTION_CONTENT_VISUALIZE_ERROR_WITH_MESSAGE, errorMessage);
            }
            transactionStoreDto.setContent(new String(visualizedContent));
        }
        return ResponseDto.returnSuccess(transactionStoreDto);
    }

}
