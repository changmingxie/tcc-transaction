package org.mengyun.tcctransaction.sample.grpc.capital;

import io.grpc.stub.StreamObserver;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.sample.capital.domain.entity.CapitalAccount;
import org.mengyun.tcctransaction.sample.capital.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.capital.domain.repository.CapitalAccountRepository;
import org.mengyun.tcctransaction.sample.capital.domain.repository.TradeOrderRepository;
import org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceGrpc;
import org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass;
import org.mengyun.tcctransaction.sample.grpc.capital.api.dto.CapitalTradeOrderDto;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Nervose.Wu
 * @date 2022/6/23 18:46
 */

@GrpcService(CapitalServiceOuterClass.class)
public class CapitalService extends CapitalServiceGrpc.CapitalServiceImplBase {

    @Autowired
    private CapitalAccountRepository capitalAccountRepository;

    @Autowired
    private TradeOrderRepository tradeOrderRepository;


    @Override
    public void getCapitalAccountByUserId(CapitalServiceOuterClass.CapitalAccountRequest request, StreamObserver<CapitalServiceOuterClass.CapitalAccountReply> responseObserver) {
        String amount = capitalAccountRepository.findByUserId(request.getUserId()).getBalanceAmount().toPlainString();
        responseObserver.onNext(CapitalServiceOuterClass.CapitalAccountReply.newBuilder().setAmount(amount).build());
        responseObserver.onCompleted();
    }

    @Override
    public void record(CapitalServiceOuterClass.CapitalTradeOrderDto tradeOrderDto, StreamObserver<CapitalServiceOuterClass.RecordReply> responseObserver) {
        CapitalTradeOrderDto capitalTradeOrderDto = new CapitalTradeOrderDto();
        capitalTradeOrderDto.setSelfUserId(tradeOrderDto.getSelfUserId());
        capitalTradeOrderDto.setOppositeUserId(tradeOrderDto.getOppositeUserId());
        capitalTradeOrderDto.setOrderTitle(tradeOrderDto.getOrderTitle());
        capitalTradeOrderDto.setMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        capitalTradeOrderDto.setAmount(new BigDecimal(tradeOrderDto.getAmount()));

        ((CapitalService) AopContext.currentProxy()).record(capitalTradeOrderDto);
        responseObserver.onNext(CapitalServiceOuterClass.RecordReply.newBuilder().setMessage("success").build());
        responseObserver.onCompleted();
    }


    @Transactional
    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord")
    public void record(CapitalTradeOrderDto tradeOrderDto){
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("capital try record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        TradeOrder foundTradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        //check if trade order has been recorded, if yes, return success directly.
        if (foundTradeOrder == null) {

            TradeOrder tradeOrder = new TradeOrder(
                    tradeOrderDto.getSelfUserId(),
                    tradeOrderDto.getOppositeUserId(),
                    tradeOrderDto.getMerchantOrderNo(),
                    tradeOrderDto.getAmount()
            );

            try {
                tradeOrderRepository.insert(tradeOrder);

                CapitalAccount transferFromAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

                transferFromAccount.transferFrom(tradeOrderDto.getAmount());

                capitalAccountRepository.save(transferFromAccount);
            } catch (DataIntegrityViolationException e) {
                //this exception may happen when insert trade order concurrently, if happened, ignore this insert operation.
            }
        }
    }


    @Transactional
    public void confirmRecord(CapitalTradeOrderDto tradeOrderDto) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("capital confirm record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

        //check if the trade order status is DRAFT, if yes, return directly, ensure idempotency.
        if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.confirm();
            tradeOrderRepository.update(tradeOrder);

            CapitalAccount transferToAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());

            transferToAccount.transferTo(tradeOrderDto.getAmount());

            capitalAccountRepository.save(transferToAccount);
        }
    }

    @Transactional
    public void cancelRecord(CapitalTradeOrderDto tradeOrderDto) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("capital cancel record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

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
