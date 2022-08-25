package org.mengyun.tcctransaction.constants;

public class RemotingServiceCode {
    public static final int CREATE = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final int FIND = 4;
    public static final int RECOVER_COMMIT = 5;
    public static final int RECOVER_ROLLBACK = 6;
    public static final int REGISTER = 7;
    public static final int DESERIALIZE_TRANSACTION = 8;

    private RemotingServiceCode() {
    }
}
