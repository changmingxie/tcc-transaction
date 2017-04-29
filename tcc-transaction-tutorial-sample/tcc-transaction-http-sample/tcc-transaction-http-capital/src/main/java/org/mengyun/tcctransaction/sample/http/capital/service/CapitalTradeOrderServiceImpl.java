package org.mengyun.tcctransaction.sample.http.capital.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.mengyun.tcctransaction.sample.http.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.sample.http.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.http.capital.domain.entity.CapitalAccount;
import org.mengyun.tcctransaction.sample.http.capital.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.http.capital.domain.repository.CapitalAccountRepository;
import org.mengyun.tcctransaction.sample.http.capital.domain.repository.TradeOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * Created by changming.xie on 4/2/16.
 */
public class CapitalTradeOrderServiceImpl implements CapitalTradeOrderService {

    @Autowired
    CapitalAccountRepository capitalAccountRepository;

    @Autowired
    TradeOrderRepository tradeOrderRepository;

    @Override
    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord", transactionContextEditor = MethodTransactionContextEditor.class)
    @Transactional
    public String record(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {

        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("capital try record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        TradeOrder tradeOrder = new TradeOrder(
                tradeOrderDto.getSelfUserId(),
                tradeOrderDto.getOppositeUserId(),
                tradeOrderDto.getMerchantOrderNo(),
                tradeOrderDto.getAmount()
        );

        tradeOrderRepository.insert(tradeOrder);

        CapitalAccount transferFromAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

        transferFromAccount.transferFrom(tradeOrderDto.getAmount());

        capitalAccountRepository.save(transferFromAccount);
        return "success";
    }

    @Transactional
    public void confirmRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {

        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("capital confirm record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.confirm();
            tradeOrderRepository.update(tradeOrder);

            CapitalAccount transferToAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());

            transferToAccount.transferTo(tradeOrderDto.getAmount());

            capitalAccountRepository.save(transferToAccount);
        }
    }

    @Transactional
    public void cancelRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {

        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("capital cancel record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.cancel();
            tradeOrderRepository.update(tradeOrder);

            CapitalAccount capitalAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

            capitalAccount.cancelTransfer(tradeOrderDto.getAmount());

            capitalAccountRepository.save(capitalAccount);
        }
    }
}
