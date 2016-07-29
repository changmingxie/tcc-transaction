package org.mengyun.tcctransaction.sample.dubbo.redpacket.service;

import org.mengyun.tcctransaction.Compensable;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.domain.entity.RedPacketAccount;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.domain.repository.RedPacketAccountRepository;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.api.RedPacketTradeOrderService;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.api.dto.RedPacketTradeOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by changming.xie on 4/2/16.
 */
@Service("redPacketTradeOrderService")
public class RedPacketTradeOrderServiceImpl implements RedPacketTradeOrderService {

    @Autowired
    RedPacketAccountRepository redPacketAccountRepository;

    @Override
    @Compensable(confirmMethod = "confirmRecord",cancelMethod = "cancelRecord")
    public String record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
        System.out.println("red packet try record called");

        RedPacketAccount transferFromAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

        transferFromAccount.transferFrom(tradeOrderDto.getAmount());

        redPacketAccountRepository.save(transferFromAccount);

        return "success";
    }

    public void confirmRecord(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
        System.out.println("red packet confirm record called");

        RedPacketAccount transferToAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());

        transferToAccount.transferTo(tradeOrderDto.getAmount());

        redPacketAccountRepository.save(transferToAccount);
    }

    public void cancelRecord(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
        System.out.println("red packet cancel record called");

        RedPacketAccount capitalAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

        capitalAccount.cancelTransfer(tradeOrderDto.getAmount());

        redPacketAccountRepository.save(capitalAccount);
    }
}
