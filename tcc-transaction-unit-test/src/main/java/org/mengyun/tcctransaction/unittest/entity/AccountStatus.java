package org.mengyun.tcctransaction.unittest.entity;

/**
 * Created by changmingxie on 12/3/15.
 */
public enum AccountStatus {

    NORMAL(1),

    TRANSFERING(2);

    private int id;

    AccountStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
