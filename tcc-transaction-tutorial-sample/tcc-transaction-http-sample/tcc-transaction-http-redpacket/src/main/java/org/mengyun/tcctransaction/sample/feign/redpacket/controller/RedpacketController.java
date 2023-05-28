package org.mengyun.tcctransaction.sample.feign.redpacket.controller;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.sample.feign.redpacket.api.dto.RedPacketTradeOrderDto;
import org.mengyun.tcctransaction.sample.redpacket.domain.entity.RedPacketAccount;
import org.mengyun.tcctransaction.sample.redpacket.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.redpacket.domain.repository.RedPacketAccountRepository;
import org.mengyun.tcctransaction.sample.redpacket.domain.repository.TradeOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 15:30
 */
@RestController
public class RedpacketController {

    @Autowired
    private RedPacketAccountRepository redPacketAccountRepository;

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    @RequestMapping(value = "/account/query", method = RequestMethod.GET)
    public BigDecimal getCapitalAccountByUserId(@RequestParam long userId) {
        return redPacketAccountRepository.findByUserId(userId).getBalanceAmount();
    }

    @RequestMapping(value = "/tradeOrder/record", method = RequestMethod.POST)
    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord")
    @Transactional
    public String record(@RequestBody RedPacketTradeOrderDto tradeOrderDto) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("red packet try record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
        TradeOrder foundTradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        //check if the trade order has need recorded.
        //if record, then this method call return success directly.
        if (foundTradeOrder == null) {
            TradeOrder tradeOrder = new TradeOrder(tradeOrderDto.getSelfUserId(), tradeOrderDto.getOppositeUserId(), tradeOrderDto.getMerchantOrderNo(), tradeOrderDto.getAmount());
            try {
                tradeOrderRepository.insert(tradeOrder);
                RedPacketAccount transferFromAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());
                transferFromAccount.transferFrom(tradeOrderDto.getAmount());
                redPacketAccountRepository.save(transferFromAccount);
            } catch (DataIntegrityViolationException e) {
            }
        }
        return "success";
    }

    @Transactional
    public void confirmRecord(RedPacketTradeOrderDto tradeOrderDto) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("red packet confirm record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.confirm();
            tradeOrderRepository.update(tradeOrder);
            RedPacketAccount transferToAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());
            transferToAccount.transferTo(tradeOrderDto.getAmount());
            redPacketAccountRepository.save(transferToAccount);
        }
    }

    @Transactional
    public void cancelRecord(RedPacketTradeOrderDto tradeOrderDto) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("red packet cancel record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.cancel();
            tradeOrderRepository.update(tradeOrder);
            RedPacketAccount capitalAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());
            capitalAccount.cancelTransfer(tradeOrderDto.getAmount());
            redPacketAccountRepository.save(capitalAccount);
        }
    }
}
