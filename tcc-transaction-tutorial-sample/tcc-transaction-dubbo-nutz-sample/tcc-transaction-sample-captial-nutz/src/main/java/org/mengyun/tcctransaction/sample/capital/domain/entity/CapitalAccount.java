package org.mengyun.tcctransaction.sample.capital.domain.entity;



import java.math.BigDecimal;

import org.mengyun.tcctransaction.sample.exception.InsufficientBalanceException;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

/**
 * Created by changming.xie on 4/2/16.
 */
@PK({"id"})
@Table("cap_capital_account")
public class CapitalAccount {
	
	@Column("CAPITAL_ACCOUNT_ID")
    private long id;
	
	@Column("USER_ID")
    private long userId;
	
	@Column("BALANCE_AMOUNT")
    private BigDecimal balanceAmount;

    private BigDecimal transferAmount = BigDecimal.ZERO;

    public long getUserId() {
        return userId;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void transferFrom(BigDecimal amount) {

        this.balanceAmount = this.balanceAmount.subtract(amount);

        if (BigDecimal.ZERO.compareTo(this.balanceAmount) > 0) {
            throw new InsufficientBalanceException();
        }

        transferAmount = transferAmount.add(amount.negate());
    }

    public void transferTo(BigDecimal amount) {
        this.balanceAmount = this.balanceAmount.add(amount);
        transferAmount = transferAmount.add(amount);
    }

    public void cancelTransfer(BigDecimal amount) {
        transferTo(amount);
    }
}
