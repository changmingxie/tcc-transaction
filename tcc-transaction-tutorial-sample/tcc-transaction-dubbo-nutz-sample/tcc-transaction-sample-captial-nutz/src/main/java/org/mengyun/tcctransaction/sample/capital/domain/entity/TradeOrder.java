package org.mengyun.tcctransaction.sample.capital.domain.entity;

import java.math.BigDecimal;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;
@PK({"id"})
@Table("cap_trade_order")
public class TradeOrder {
	@Id
	@Column("id")
    private long id;
	
	@Column("self_user_id")
    private long selfUserId;
	
	@Column("OPPOSITE_USER_ID")
    private long oppositeUserId;
	
	@Column("MERCHANT_ORDER_NO")
    private String merchantOrderNo;
	
	@Column("AMOUNT")
    private BigDecimal amount;

	@Column("STATUS")
    private String status = "DRAFT";
	
	@Column(value="VERSION",version=true)
    private long version = 1l;

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

    public void confirm() {
        this.status = "CONFIRM";
    }

    public void cancel() {
        this.status = "CANCEL";
    }

    public long getVersion() {
        return version;
    }

    public void updateVersion() {
        this.version = version + 1;
    }
}
