package org.mengyun.tcctransaction.remoting.protocol;

import java.util.concurrent.atomic.AtomicInteger;

public class RemotingCommand {

    private static AtomicInteger requestIdGenerator = new AtomicInteger(0);

    private int requestId = requestIdGenerator.getAndIncrement();
    private RemotingCommandCode code;
    private String remark;
    private int serviceCode;
    private byte[] body;

    /**
     * temporary information used for statistics
     */
    private transient long receiveTime;

    private transient RemotingCommand originalCommand;

    public static RemotingCommand createServiceResponseCommand(String remark) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(RemotingCommandCode.SERVICE_RESP);
        cmd.setRemark(remark);
        return cmd;
    }

    public static RemotingCommand createCommand(RemotingCommandCode code, String remark) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(code);
        cmd.setRemark(remark);
        return cmd;
    }

    public int getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(int serviceCode) {
        this.serviceCode = serviceCode;
    }


    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public RemotingCommandCode getCode() {
        return code;
    }

    public void setCode(RemotingCommandCode code) {
        this.code = code;
    }


    public boolean isRequestCommand() {
        return code.isRequestCode();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public RemotingCommand getOriginalCommand() {
        return originalCommand;
    }

    public void setOriginalCommand(RemotingCommand originalCommand) {
        this.originalCommand = originalCommand;
    }
}