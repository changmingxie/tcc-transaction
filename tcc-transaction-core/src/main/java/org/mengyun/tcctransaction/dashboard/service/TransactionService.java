package org.mengyun.tcctransaction.dashboard.service;

import org.mengyun.tcctransaction.dashboard.dto.*;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 10:08
 **/
public interface TransactionService extends TransactionStorageable {

    /**
     * 事件分页查询
     *
     * @param requestDto
     * @return
     */
    public ResponseDto<TransactionPageDto> list(TransactionPageRequestDto requestDto);

    public ResponseDto<TransactionStoreDto> detail(TransactionDetailRequestDto requestDto);

    public ResponseDto confirm(TransactionOperateRequestDto requestDto);

    public ResponseDto cancel(TransactionOperateRequestDto requestDto);

    public ResponseDto reset(TransactionOperateRequestDto requestDto);

    public ResponseDto markDeleted(TransactionOperateRequestDto requestDto);

    public ResponseDto restore(TransactionOperateRequestDto requestDto);

    public ResponseDto delete(TransactionOperateRequestDto requestDto);

}
