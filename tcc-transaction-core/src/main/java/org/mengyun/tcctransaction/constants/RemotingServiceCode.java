package org.mengyun.tcctransaction.constants;

import java.util.HashMap;
import java.util.Map;

public class RemotingServiceCode {

    public static final int SYSTEM = 0;
    public static final int CREATE = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final int FIND = 4;
    public static final int RECOVER_COMMIT = 5;
    public static final int RECOVER_ROLLBACK = 6;
    public static final int REGISTER = 7;
    public static final int DESERIALIZE_TRANSACTION = 8;

    private static Map<Integer, String> codeToDesc = new HashMap<>();

    static {
        codeToDesc.put(SYSTEM, "SYSTEM");
        codeToDesc.put(CREATE, "CREATE");
        codeToDesc.put(UPDATE, "UPDATE");
        codeToDesc.put(DELETE, "DELETE");
        codeToDesc.put(FIND, "FIND");
        codeToDesc.put(RECOVER_COMMIT, "RECOVER_COMMIT");
        codeToDesc.put(RECOVER_ROLLBACK, "RECOVER_ROLLBACK");
        codeToDesc.put(REGISTER, "REGISTER");
        codeToDesc.put(DESERIALIZE_TRANSACTION, "DESERIALIZE_TRANSACTION");
    }

    private RemotingServiceCode() {
    }

    public static String getDesc(int code) {
        return codeToDesc.getOrDefault(code, "UNKNOWN");
    }
}
