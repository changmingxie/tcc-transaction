package org.mengyun.tcctransaction.dashboard.service.impl;

import org.mengyun.tcctransaction.dashboard.dto.DomainStoreDto;
import org.mengyun.tcctransaction.dashboard.dto.DomainStoreRequestDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.exception.TransactionException;
import org.mengyun.tcctransaction.dashboard.service.DomainService;
import org.mengyun.tcctransaction.storage.StorageRecoverable;
import org.mengyun.tcctransaction.storage.domain.AlertType;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.utils.TccDateFormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author huabao.fang
 * @Date 2022/6/9 17:03
 **/
public abstract class BaseDomainServiceImpl implements DomainService {

    @Override
    public ResponseDto<List<String>> getAllDomainKeys() {
        List<String> list = getRecoverableTransactionStorage().getAllDomains().stream().map(DomainStore::getDomain).collect(Collectors.toList());
        return ResponseDto.returnSuccess(list);
    }

    @Override
    public ResponseDto<List<DomainStoreDto>> getAllDomains() {
        return ResponseDto.returnSuccess(transferToDomainStoreDtoList(getRecoverableTransactionStorage().getAllDomains()));
    }

    @Override
    public ResponseDto<Void> create(DomainStoreRequestDto requestDto) {
        getRecoverableTransactionStorage().registerDomain(transferToNewDomainStore(requestDto, null));
        return ResponseDto.returnSuccess();
    }

    @Override
    public ResponseDto<Void> modify(DomainStoreRequestDto requestDto) {
        DomainStore record = getRecoverableTransactionStorage().findDomain(requestDto.getDomain());
        if (record == null) {
            throw new TransactionException(ResponseCodeEnum.DOMAIN_NOT_EXIST);
        }
        getRecoverableTransactionStorage().updateDomain(transferToNewDomainStore(requestDto, record));
        return ResponseDto.returnSuccess();
    }

    @Override
    public ResponseDto<Void> delete(DomainStoreRequestDto requestDto) {
        DomainStore record = getRecoverableTransactionStorage().findDomain(requestDto.getDomain());
        if (record == null) {
            throw new TransactionException(ResponseCodeEnum.DOMAIN_NOT_EXIST);
        }
        getRecoverableTransactionStorage().removeDomain(requestDto.getDomain());
        return ResponseDto.returnSuccess();
    }

    private StorageRecoverable getRecoverableTransactionStorage() {
        return (StorageRecoverable) getTransactionStorage();
    }

    private DomainStore transferToNewDomainStore(DomainStoreRequestDto requestDto, DomainStore sourceDomainStore) {
        if (sourceDomainStore == null) {
            sourceDomainStore = new DomainStore();
        }
        sourceDomainStore.setDomain(requestDto.getDomain());
        sourceDomainStore.setPhoneNumbers(requestDto.getPhoneNumbers());
        sourceDomainStore.setAlertType(AlertType.nameOf(requestDto.getAlertType()));
        sourceDomainStore.setThreshold(requestDto.getThreshold());
        sourceDomainStore.setIntervalMinutes(requestDto.getIntervalMinutes());
        sourceDomainStore.setDingRobotUrl(requestDto.getDingRobotUrl());
        return sourceDomainStore;
    }

    private List<DomainStoreDto> transferToDomainStoreDtoList(List<DomainStore> domainStoreList) {
        List<DomainStoreDto> domainStoreDtoList = new ArrayList<>(domainStoreList.size());
        //按更新时间降序
        domainStoreList.sort(((d1, d2) -> d2.getLastUpdateTime().compareTo(d1.getLastUpdateTime())));
        for (DomainStore domainStore : domainStoreList) {
            domainStoreDtoList.add(transferToDomainStoreDto(domainStore));
        }
        domainStoreDtoList.sort((d1, d2) -> d2.getCreateTime().compareTo(d1.getCreateTime()));
        return domainStoreDtoList;
    }

    private DomainStoreDto transferToDomainStoreDto(DomainStore domainStore) {
        DomainStoreDto domainStoreDto = new DomainStoreDto();
        domainStoreDto.setDomain(domainStore.getDomain());
        domainStoreDto.setPhoneNumbers(domainStore.getPhoneNumbers());
        domainStoreDto.setAlertType(domainStore.getAlertType() == null ? null : domainStore.getAlertType().name());
        domainStoreDto.setThreshold(domainStore.getThreshold());
        domainStoreDto.setIntervalMinutes(domainStore.getIntervalMinutes());
        domainStoreDto.setLastAlertTime(TccDateFormatUtils.formatDate(domainStore.getLastAlertTime()));
        domainStoreDto.setDingRobotUrl(domainStore.getDingRobotUrl());
        domainStoreDto.setCreateTime(TccDateFormatUtils.formatDate(domainStore.getCreateTime()));
        domainStoreDto.setLastUpdateTime(TccDateFormatUtils.formatDate(domainStore.getLastUpdateTime()));
        domainStoreDto.setVersion(domainStore.getVersion());
        return domainStoreDto;
    }

}
