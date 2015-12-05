package org.mengyun.tcctransaction.unittest.entity;

/**
 * Created by changmingxie on 12/3/15.
 */
public class SubAccount {

    private long id;

    private volatile int balanceAmount;

    private volatile int status = AccountStatus.NORMAL.getId();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(int balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public SubAccount(long id, int balanceAmount) {
        this.id = id;
        this.balanceAmount = balanceAmount;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
