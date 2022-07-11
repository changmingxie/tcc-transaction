package org.mengyun.tcctransaction.dashboard.controller;

import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.dto.TransactionDetailRequestDto;
import org.mengyun.tcctransaction.dashboard.dto.TransactionOperateRequestDto;
import org.mengyun.tcctransaction.dashboard.dto.TransactionPageRequestDto;
import org.mengyun.tcctransaction.dashboard.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 14:25
 **/
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping("/list")
    @ResponseBody
    public ResponseDto list(@RequestBody TransactionPageRequestDto requestDto) {
        return transactionService.list(requestDto);
    }

    @RequestMapping("/detail")
    @ResponseBody
    public ResponseDto detail(@RequestBody TransactionDetailRequestDto requestDto) {
        return transactionService.detail(requestDto);
    }

    @RequestMapping("/confirm")
    @ResponseBody
    public ResponseDto confirm(@RequestBody TransactionOperateRequestDto requestDto) {
        return transactionService.confirm(requestDto);
    }

    @RequestMapping("/cancel")
    @ResponseBody
    public ResponseDto cancel(@RequestBody TransactionOperateRequestDto requestDto) {
        return transactionService.cancel(requestDto);
    }

    @RequestMapping("/reset")
    public ResponseDto reset(@RequestBody TransactionOperateRequestDto requestDto) {
        return transactionService.reset(requestDto);
    }


    @RequestMapping("/markDeleted")
    @ResponseBody
    public ResponseDto markDeleted(@RequestBody TransactionOperateRequestDto requestDto) {
        return transactionService.markDeleted(requestDto);
    }

    @RequestMapping("/restore")
    @ResponseBody
    public ResponseDto restore(@RequestBody TransactionOperateRequestDto requestDto) {
        return transactionService.restore(requestDto);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResponseDto delete(@RequestBody TransactionOperateRequestDto requestDto) {
        return transactionService.delete(requestDto);
    }

}
