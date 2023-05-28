package org.mengyun.tcctransaction.sample.grpc.redpacket;

import io.grpc.stub.StreamObserver;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceGrpc;
import org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass;
import org.mengyun.tcctransaction.sample.grpc.redpacket.api.dto.RedPacketTradeOrderDto;
import org.mengyun.tcctransaction.sample.redpacket.domain.entity.RedPacketAccount;
import org.mengyun.tcctransaction.sample.redpacket.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.redpacket.domain.repository.RedPacketAccountRepository;
import org.mengyun.tcctransaction.sample.redpacket.domain.repository.TradeOrderRepository;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Nervose.Wu
 * @date 2022/6/23 17:45
 */
@GrpcService(RedPacketServiceOuterClass.class)
public class RedPacketService extends RedPacketServiceGrpc.RedPacketServiceImplBase {

    @Autowired
    private RedPacketAccountRepository redPacketAccountRepository;

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    @Override
    public void getRedPacketAccountByUserId(RedPacketServiceOuterClass.RedPacketAccountRequest request, StreamObserver<RedPacketServiceOuterClass.RedPacketAccountReply> responseObserver) {
        String amount = redPacketAccountRepository.findByUserId(request.getUserId()).getBalanceAmount().toPlainString();
        responseObserver.onNext(RedPacketServiceOuterClass.RedPacketAccountReply.newBuilder().setAmount(amount).build());
        responseObserver.onCompleted();
    }

    @Override
    public void record(RedPacketServiceOuterClass.RedPacketTradeOrderDto tradeOrderDto, StreamObserver<RedPacketServiceOuterClass.RecordReply> responseObserver) {
        RedPacketTradeOrderDto redPacketTradeOrderDto = new RedPacketTradeOrderDto();
        redPacketTradeOrderDto.setSelfUserId(tradeOrderDto.getSelfUserId());
        redPacketTradeOrderDto.setOppositeUserId(tradeOrderDto.getOppositeUserId());
        redPacketTradeOrderDto.setOrderTitle(tradeOrderDto.getOrderTitle());
        redPacketTradeOrderDto.setMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        redPacketTradeOrderDto.setAmount(new BigDecimal(tradeOrderDto.getAmount()));
        ((RedPacketService) AopContext.currentProxy()).record(redPacketTradeOrderDto);
        responseObserver.onNext(RedPacketServiceOuterClass.RecordReply.newBuilder().setMessage("success").build());
        responseObserver.onCompleted();
    }

    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord")
    @Transactional
    public void record(RedPacketTradeOrderDto tradeOrderDto) {
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
