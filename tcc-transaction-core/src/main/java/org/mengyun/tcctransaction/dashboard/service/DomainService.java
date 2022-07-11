package org.mengyun.tcctransaction.dashboard.service;


import org.mengyun.tcctransaction.dashboard.dto.DomainStoreDto;
import org.mengyun.tcctransaction.dashboard.dto.DomainStoreRequestDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;

import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 10:09
 **/
public interface DomainService extends TransactionStorageable{

    public ResponseDto<List<String>> getAllDomainKeys();

    public ResponseDto<List<DomainStoreDto>> getAllDomains();

    public ResponseDto create(DomainStoreRequestDto requestDto);

    public ResponseDto modify(DomainStoreRequestDto requestDto);

    public ResponseDto delete(DomainStoreRequestDto requestDto);

}
