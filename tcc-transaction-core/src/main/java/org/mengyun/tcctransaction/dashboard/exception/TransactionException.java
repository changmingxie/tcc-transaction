package org.mengyun.tcctransaction.dashboard.exception;

import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;

/**
 * @Author huabao.fang
 * @Date 2022/6/7 00:28
 * 命名需要调整 TODO
 **/
public class TransactionException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    public TransactionException() {
        super();
    }

    public TransactionException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public TransactionException(ResponseCodeEnum responseCodeEnum) {
        this(responseCodeEnum.getCode(), responseCodeEnum.getMessage());
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getMessage() {
        return this.errorCode + "-" + this.errorMessage;
    }
}
