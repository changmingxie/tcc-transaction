package org.mengyun.tcctransaction.dashboard.service;

import org.mengyun.tcctransaction.dashboard.dto.DomainStoreDto;
import org.mengyun.tcctransaction.dashboard.dto.DomainStoreRequestDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 10:09
 */
public interface DomainService extends TransactionStorageable {

    ResponseDto<List<String>> getAllDomainKeys();

    ResponseDto<List<DomainStoreDto>> getAllDomains();

    ResponseDto<Void> create(DomainStoreRequestDto requestDto);

    ResponseDto<Void> modify(DomainStoreRequestDto requestDto);

    ResponseDto<Void> delete(DomainStoreRequestDto requestDto);
}
