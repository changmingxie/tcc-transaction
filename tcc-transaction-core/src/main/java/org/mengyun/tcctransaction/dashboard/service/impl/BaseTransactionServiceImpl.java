package org.mengyun.tcctransaction.dashboard.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.dashboard.dto.*;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.service.TransactionService;
import org.mengyun.tcctransaction.storage.Page;
import org.mengyun.tcctransaction.storage.StorageRecoverable;
import org.mengyun.tcctransaction.storage.TransactionStore;
import org.mengyun.tcctransaction.storage.helper.RedisHelper;
import org.mengyun.tcctransaction.utils.TccDateFormatUtils;
import org.mengyun.tcctransaction.xid.TransactionXid;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/6/9 16:27
 **/
public abstract class BaseTransactionServiceImpl implements TransactionService {

    private static final int DEFAULT_PAGE_SIZE = RedisHelper.SCAN_MIDDLE_COUNT;

    /**
     * 事件分页查询
     *
     * @param requestDto
     * @return
     */
    @Override
    public ResponseDto<TransactionPageDto> list(TransactionPageRequestDto requestDto) {
        Page<TransactionStore> page = null;
        int total = 0;
        if (!StringUtils.isEmpty(requestDto.getXidString())) {
            page = findByXid(requestDto);
            total = page.getData().size();
        } else {
            page = findPage(requestDto);
            total = findTotal(requestDto);
        }
        TransactionPageDto pageDto = new TransactionPageDto();
        pageDto.setNextOffset(page.getNextOffset());
        pageDto.setItems(toTransactionStoreDtoList(page.getData()));
        pageDto.setTotal(total);
        return ResponseDto.returnSuccess(pageDto);
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
            String base64Content = Base64.getEncoder().encodeToString(transactionStore.getContent());
            transactionStoreDto.setContent(base64Content);
        }
        return ResponseDto.returnSuccess(transactionStoreDto);
    }

    private int findTotal(TransactionPageRequestDto requestDto) {
        StorageRecoverable transactionStorage = (StorageRecoverable) getTransactionStorage();
        return transactionStorage.count(requestDto.getDomain(), requestDto.isMarkDeleted());
    }

    private Page<TransactionStore> findByXid(TransactionPageRequestDto requestDto) {
        TransactionStore transactionStore = null;
        List<TransactionStore> list = new ArrayList<>();
        if (requestDto.isMarkDeleted()) {
            transactionStore = getTransactionStorage().findMarkDeletedByXid(requestDto.getDomain(), new TransactionXid(requestDto.getXidString()));
        } else {
            transactionStore = getTransactionStorage().findByXid(requestDto.getDomain(), new TransactionXid(requestDto.getXidString()));
        }
        if (transactionStore != null) {
            list.add(transactionStore);
        }
        return new Page<>(null, list);
    }

    private Page<TransactionStore> findPage(TransactionPageRequestDto requestDto) {
        StorageRecoverable transactionStorage = (StorageRecoverable) getTransactionStorage();
        int pageSize = Math.max(requestDto.getPageSize(), DEFAULT_PAGE_SIZE);
        Page<TransactionStore> page;
        if (requestDto.isMarkDeleted()) {
            page = transactionStorage.findAllDeletedSince(requestDto.getDomain(), new Date(), requestDto.getOffset(), pageSize);
        } else {
            page = transactionStorage.findAllUnmodifiedSince(requestDto.getDomain(), new Date(), requestDto.getOffset(), pageSize);
        }
        return page;
    }

    @Override
    public ResponseDto<Void> confirm(TransactionOperateRequestDto requestDto) {
        TransactionStore transactionStore = getTransactionStorage().findByXid(requestDto.getDomain(), new TransactionXid(requestDto.getXidString()));
        transactionStore.setStatusId(TransactionStatus.CONFIRMING.getId());
        getTransactionStorage().update(transactionStore);
        return ResponseDto.returnSuccess();
    }

    @Override
    public ResponseDto<Void> cancel(TransactionOperateRequestDto requestDto) {
        TransactionStore transactionStore = getTransactionStorage().findByXid(requestDto.getDomain(), new TransactionXid(requestDto.getXidString()));
        transactionStore.setStatusId(TransactionStatus.CANCELLING.getId());
        getTransactionStorage().update(transactionStore);
        return ResponseDto.returnSuccess();
    }

    @Override
    public ResponseDto<Void> reset(TransactionOperateRequestDto requestDto) {
        TransactionStore transactionStore = getTransactionStorage().findByXid(requestDto.getDomain(), new TransactionXid(requestDto.getXidString()));
        transactionStore.setRetriedCount(0);
        getTransactionStorage().update(transactionStore);
        return ResponseDto.returnSuccess();
    }

    /**
     * 软删除，事件标记为删除状态
     *
     * @param requestDto
     */
    @Override
    public ResponseDto<Void> markDeleted(TransactionOperateRequestDto requestDto) {
        TransactionStore transactionStore = getTransactionStorage().findByXid(requestDto.getDomain(), new TransactionXid(requestDto.getXidString()));
        getTransactionStorage().markDeleted(transactionStore);
        return ResponseDto.returnSuccess();
    }

    /**
     * 软删除状态恢复为正常
     *
     * @param requestDto
     */
    @Override
    public ResponseDto<Void> restore(TransactionOperateRequestDto requestDto) {
        TransactionStore transactionStore = getTransactionStorage().findMarkDeletedByXid(requestDto.getDomain(), new TransactionXid(requestDto.getXidString()));
        getTransactionStorage().restore(transactionStore);
        return ResponseDto.returnSuccess();
    }

    /**
     * 物理删除，即彻底删除，对标记为删除的事件
     *
     * @param requestDto
     */
    @Override
    public ResponseDto<Void> delete(TransactionOperateRequestDto requestDto) {
        TransactionStore transactionStore = getTransactionStorage().findMarkDeletedByXid(requestDto.getDomain(), new TransactionXid(requestDto.getXidString()));
        getTransactionStorage().completelyDelete(transactionStore);
        return ResponseDto.returnSuccess();
    }


    private List<TransactionStoreDto> toTransactionStoreDtoList(List<TransactionStore> transactionStoreList) {
        List<TransactionStoreDto> transactionStoreDtoList = new ArrayList<>(transactionStoreList.size());
        transactionStoreList.forEach(transactionStore -> {
            transactionStoreDtoList.add(toTransactionStoreDto(transactionStore));
        });
        transactionStoreDtoList.sort((t1, t2) -> {
            return -1 * t1.getCreateTime().compareTo(t2.getCreateTime());
        });
        return transactionStoreDtoList;
    }

    protected TransactionStoreDto toTransactionStoreDto(TransactionStore transactionStore) {
        TransactionStoreDto transactionStoreDto = new TransactionStoreDto();
        transactionStoreDto.setDomain(transactionStore.getDomain());
        transactionStoreDto.setXid(transactionStore.getXid());
        transactionStoreDto.setRootXid(transactionStore.getRootXid());
        transactionStoreDto.setRootDomain(transactionStore.getRootDomain());
        transactionStoreDto.setContent(new String(transactionStore.getContent()));
        transactionStoreDto.setCreateTime(TccDateFormatUtils.formatDate(transactionStore.getCreateTime()));
        transactionStoreDto.setLastUpdateTime(TccDateFormatUtils.formatDate(transactionStore.getLastUpdateTime()));
        transactionStoreDto.setVersion(transactionStore.getVersion());
        transactionStoreDto.setRetriedCount(transactionStore.getRetriedCount());
        transactionStoreDto.setStatusId(transactionStore.getStatusId());
        transactionStoreDto.setTransactionTypeId(transactionStore.getTransactionTypeId());

        transactionStoreDto.setXidString(transactionStore.getXid().toString());
        transactionStoreDto.setRootXidString(transactionStore.getRootXid().toString());
        return transactionStoreDto;
    }

    protected boolean isJSONString(String content) {
        boolean isJSON = false;
        if (StringUtils.isNotBlank(content)) {
            content = content.trim();
            if (content.startsWith("{") && content.endsWith("}")) {
                isJSON = true;
            } else if (content.startsWith("[") && content.endsWith("]")) {
                isJSON = true;
            }
        }
        return isJSON;
    }
}
