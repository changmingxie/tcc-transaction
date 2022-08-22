package org.mengyun.tcctransaction.remoting.netty;

import org.mengyun.tcctransaction.remoting.protocol.RemotingCommand;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResponseFuture {
    private boolean sendRequestSuccess;
    private Throwable cause;
    private Object response;
    private RemotingCommand responseCommand;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public boolean isSendRequestSuccess() {
        return sendRequestSuccess;
    }

    public void setSendRequestSuccess(boolean sendRequestSuccess) {
        this.sendRequestSuccess = sendRequestSuccess;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public RemotingCommand get(long timoutMillis) throws InterruptedException {
        this.countDownLatch.await(timoutMillis, TimeUnit.MILLISECONDS);
        return this.responseCommand;
    }

    public void putResponse(RemotingCommand cmd) {
        this.responseCommand = cmd;
        this.countDownLatch.countDown();
    }
}
