package org.mengyun.tcctransaction.dashboard.service.impl.tccserver;

import org.mengyun.tcctransaction.dashboard.constants.DashboardConstant;
import org.mengyun.tcctransaction.dashboard.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 10:10
 **/
@FeignClient(value = DashboardConstant.TCC_SERVER_GROUP, path = "${feign.path:/tcc-transaction-server}")
public interface TccServerFeignClient {

    @RequestMapping("/domain/allDomainKeys")
    ResponseDto<List<String>> allDomainKeys();

    @RequestMapping("/domain/all")
    @ResponseBody
    ResponseDto<List<DomainStoreDto>> all();

    @RequestMapping("/domain/create")
    @ResponseBody
    ResponseDto<Void> createDomain(@RequestBody DomainStoreRequestDto requestDto);

    @RequestMapping("/domain/modify")
    @ResponseBody
    ResponseDto<Void> modifyDomain(@RequestBody DomainStoreRequestDto requestDto);

    @RequestMapping("/domain/delete")
    @ResponseBody
    ResponseDto<Void> deleteDomain(@RequestBody DomainStoreRequestDto requestDto);


    /**
     * 事件分页查询
     *
     * @param requestDto
     * @return
     */
    @RequestMapping("/transaction/list")
    @ResponseBody
    ResponseDto<TransactionPageDto> transactionList(@RequestBody TransactionPageRequestDto requestDto);

    @RequestMapping("/transaction/confirm")
    @ResponseBody
    ResponseDto<Void> transactionConfirm(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/cancel")
    @ResponseBody
    ResponseDto<Void> transactionCancel(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/reset")
    @ResponseBody
    ResponseDto<Void> transactionReset(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/markDeleted")
    @ResponseBody
    ResponseDto<Void> transactionMarkDeleted(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/restore")
    @ResponseBody
    ResponseDto<Void> transactionRestore(@RequestBody TransactionOperateRequestDto requestDto);

    @RequestMapping("/transaction/delete")
    @ResponseBody
    ResponseDto<Void> transactionDelete(@RequestBody TransactionOperateRequestDto requestDto);

}
