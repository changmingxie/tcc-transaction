package org.mengyun.tcctransaction.sample.http.redpacket.domain.entity;

import java.math.BigDecimal;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
public class TradeOrder {

    private long id;

    private long selfUserId;

    private long oppositeUserId;

    private String merchantOrderNo;

    private BigDecimal amount;

    private String status = "DRAFT";

    public TradeOrder() {
    }

    public TradeOrder(long selfUserId, long oppositeUserId, String merchantOrderNo, BigDecimal amount) {
        this.selfUserId = selfUserId;
        this.oppositeUserId = oppositeUserId;
        this.merchantOrderNo = merchantOrderNo;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public long getSelfUserId() {
        return selfUserId;
    }

    public long getOppositeUserId() {
        return oppositeUserId;
    }

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void confirm(){
        this.status = "CONFIRM";
    }

    public void cancel(){
        this.status = "CANCEL";
    }
}
