package org.mengyun.tcctransaction.dashboard.service.impl.tccserver;

import org.mengyun.tcctransaction.dashboard.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 10:10
 * ,url = "http://localhost:9998"
 **/
@FeignClient(value = "tcc-transaction-server")
public interface TccServerFeignClient {

    @RequestMapping("/domain/allDomainKeys")
    public ResponseDto<List<String>> allDomainKeys();

    @RequestMapping("/domain/all")
    @ResponseBody
    public ResponseDto<List<DomainStoreDto>> all();

    @RequestMapping("/domain/create")
    @ResponseBody
    public ResponseDto createDomain(@RequestBody DomainStoreRequestDto requestDto);

    @RequestMapping("/domain/modify")
    @ResponseBody
    public ResponseDto modifyDomain(@RequestBody DomainStoreRequestDto requestDto);

    @RequestMapping("/domain/delete")
    @ResponseBody
    public ResponseDto deleteDomain(@RequestBody DomainStoreRequestDto requestDto);


    /**
     * 事件分页查询
     *
     * @param requestDto
     * @return
     */
    @RequestMapping("/transaction/list")
    @ResponseBody
    public ResponseDto transactionList(@RequestBody TransactionPageRequestDto requestDto);

    @RequestMapping("/transaction/confirm")
    @ResponseBody
    public ResponseDto transactionConfirm(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/cancel")
    @ResponseBody
    public ResponseDto transactionCancel(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/reset")
    @ResponseBody
    public ResponseDto transactionReset(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/markDeleted")
    @ResponseBody
    public ResponseDto transactionMarkDeleted(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/restore")
    @ResponseBody
    public ResponseDto transactionRestore(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/delete")
    @ResponseBody
    public ResponseDto transactionDelete(@RequestBody TransactionOperateRequestDto requestDto);

}
