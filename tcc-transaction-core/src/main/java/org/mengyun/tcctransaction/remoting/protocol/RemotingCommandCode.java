package org.mengyun.tcctransaction.remoting.protocol;

public enum RemotingCommandCode {

    SERVICE_REQ((byte) 0, true),
    /*业务请求消息*/
    SERVICE_RESP((byte) 1, false),
    /*TWO_WAY消息，需要业务应答*/
    HEARTBEAT_REQ((byte) 5, true),
    /*心跳请求消息*/
    HEARTBEAT_RESP((byte) 6, false),
    /*心跳应答消息*/
    SYSTEM_BUSY_RESP((byte) 7, false),
    SYSTEM_EXCEPTION_RESP((byte) 8, false);

    private byte value;

    private boolean isRequestCode;

    private RemotingCommandCode(byte value, boolean isRequestCode) {
        this.value = value;
        this.isRequestCode = isRequestCode;
    }

    public static RemotingCommandCode valueOf(byte value) {
        if (value == SERVICE_REQ.value) {
            return SERVICE_REQ;
        } else if (value == SERVICE_RESP.value) {
            return SERVICE_RESP;
        } else if (value == HEARTBEAT_REQ.value) {
            return HEARTBEAT_REQ;
        } else if (value == HEARTBEAT_RESP.value) {
            return HEARTBEAT_RESP;
        } else if (value == SYSTEM_BUSY_RESP.value()) {
            return SYSTEM_BUSY_RESP;
        } else if (value == SYSTEM_EXCEPTION_RESP.value()) {
            return SYSTEM_EXCEPTION_RESP;
        }
        throw new IllegalArgumentException(String.format("unknown RemotingCommand Type of value :%d", value));
    }

    public byte value() {
        return this.value;
    }

    public boolean isRequestCode() {
        return isRequestCode;
    }
}
