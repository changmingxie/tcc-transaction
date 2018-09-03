package org.mengyun.tcctransaction.sample.dubbo.capital.service;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.dubbo.context.DubboTransactionContextEditor;
import org.mengyun.tcctransaction.sample.capital.domain.entity.CapitalAccount;
import org.mengyun.tcctransaction.sample.capital.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.capital.domain.repository.CapitalAccountRepository;
import org.mengyun.tcctransaction.sample.capital.domain.repository.TradeOrderRepository;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.dto.CapitalTradeOrderDto;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by changming.xie on 4/2/16.
 */
@IocBean(name="capitalTradeOrderService")
public class CapitalTradeOrderServiceImpl implements CapitalTradeOrderService {

    @Inject
    CapitalAccountRepository capitalAccountRepository;

    @Inject
    TradeOrderRepository tradeOrderRepository;
    private Logger logger = Logger.getLogger(getClass());
    
    
    @Override
    @Aop({"compensableTransactionForNutzInterceptor","resourceCoordinatorForNutzInterceptor"}) 
    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord", transactionContextEditor = DubboTransactionContextEditor.class)
    // @Transactional
    public String record(CapitalTradeOrderDto tradeOrderDto) {
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }// throw new RuntimeException("我要异常");

        logger.info("capital try record called. ");

        /*if(1==1){
        	logger.info("CapitalTradeOrderServiceImpl.record 发生异常");
        	throw new RuntimeException("CapitalTradeOrderServiceImpl.record 发生异常"); 
        }*/
        TradeOrder foundTradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());


        //check if trade order has been recorded, if yes, return success directly.
        if (foundTradeOrder == null) {

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
        }

        return "success";
    }

    // @Transactional
    public void confirmRecord(CapitalTradeOrderDto tradeOrderDto) {
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);//throw new RuntimeException("33");
        }
        logger.info("capital confirm record called. ");

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        
        //check if the trade order status is DRAFT, if yes, return directly, ensure idempotency.
        if (tradeOrder != null && tradeOrder.getStatus().equals("DRAFT")) {
            tradeOrder.confirm();
            tradeOrderRepository.update(tradeOrder);

            CapitalAccount transferToAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());

            transferToAccount.transferTo(tradeOrderDto.getAmount());

            capitalAccountRepository.save(transferToAccount);
        }
    }

    // @Transactional
    public void cancelRecord(CapitalTradeOrderDto tradeOrderDto) {
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.info("capital cancel record called. ");

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        //check if the trade order status is DRAFT, if yes, return directly, ensure idempotency.
        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.cancel();
            tradeOrderRepository.update(tradeOrder);

            CapitalAccount capitalAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

            capitalAccount.cancelTransfer(tradeOrderDto.getAmount());

            capitalAccountRepository.save(capitalAccount);
        }
    }
}
