package org.mengyun.tcctransaction.dashboard.service.impl.tccserver;

import org.mengyun.tcctransaction.dashboard.dto.*;
import org.mengyun.tcctransaction.dashboard.service.TransactionService;
import org.mengyun.tcctransaction.dashboard.service.condition.TccServerStorageCondition;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 14:19
 **/
@Conditional(TccServerStorageCondition.class)
@Service
public class TccServerTransactionServiceImpl implements TransactionService {

    @Autowired
    private TccServerFeignClient tccServerFeignClient;

    @Override
    public ResponseDto<TransactionPageDto> list(TransactionPageRequestDto requestDto) {
        return tccServerFeignClient.transactionList(requestDto);
    }

    @Override
    public ResponseDto<TransactionStoreDto> detail(TransactionDetailRequestDto requestDto) {
        throw new RuntimeException("not support");
    }

    @Override
    public ResponseDto<Void> confirm(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionConfirm(requestDto);
    }

    @Override
    public ResponseDto<Void> cancel(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionCancel(requestDto);
    }

    @Override
    public ResponseDto<Void> reset(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionReset(requestDto);
    }

    @Override
    public ResponseDto<Void> markDeleted(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionMarkDeleted(requestDto);
    }

    @Override
    public ResponseDto<Void> restore(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionRestore(requestDto);
    }

    @Override
    public ResponseDto<Void> delete(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionDelete(requestDto);
    }

    @Override
    public TransactionStorage getTransactionStorage() {
        return null;
    }
}
