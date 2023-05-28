package org.mengyun.tcctransaction.dashboard.service.impl.tccserver;

import org.mengyun.tcctransaction.dashboard.dto.DomainStoreDto;
import org.mengyun.tcctransaction.dashboard.dto.DomainStoreRequestDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.service.DomainService;
import org.mengyun.tcctransaction.dashboard.service.condition.TccServerStorageCondition;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 14:17
 */
@Conditional(TccServerStorageCondition.class)
@Service
public class TccServerDomainServiceImpl implements DomainService {

    @Autowired
    private TccServerFeignClient tccServerFeignClient;

    @Override
    public ResponseDto<List<String>> getAllDomainKeys() {
        return tccServerFeignClient.allDomainKeys();
    }

    @Override
    public ResponseDto<List<DomainStoreDto>> getAllDomains() {
        return tccServerFeignClient.all();
    }

    @Override
    public ResponseDto<Void> create(DomainStoreRequestDto requestDto) {
        return tccServerFeignClient.createDomain(requestDto);
    }

    @Override
    public ResponseDto<Void> modify(DomainStoreRequestDto requestDto) {
        return tccServerFeignClient.modifyDomain(requestDto);
    }

    @Override
    public ResponseDto<Void> delete(DomainStoreRequestDto requestDto) {
        return tccServerFeignClient.deleteDomain(requestDto);
    }

    @Override
    public TransactionStorage getTransactionStorage() {
        return null;
    }
}
