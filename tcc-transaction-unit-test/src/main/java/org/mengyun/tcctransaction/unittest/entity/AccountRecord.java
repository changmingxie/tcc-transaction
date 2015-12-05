package org.mengyun.tcctransaction.unittest.entity;

/**
 * Created by changmingxie on 12/3/15.
 */
public class AccountRecord {

    private long accountId;

    private  volatile int balanceAmount;

    private volatile int statusId = AccountStatus.NORMAL.getId();

    public AccountRecord(long accountId, int balanceAmount) {
        this.accountId = accountId;
        this.balanceAmount = balanceAmount;
    }

    public long getAccountId() {
        return accountId;
    }

    public int getBalanceAmount() {
        return balanceAmount;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public void setBalanceAmount(int balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public int getStatusId() {
        return statusId;
    }
}
