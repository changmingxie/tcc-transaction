package org.mengyun.tcctransaction.dashboard.exceptionhandler;

import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author huabao.fang
 * @Date 2022/6/7 00:26
 **/
@ControllerAdvice
public class ControllerExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(TransactionException.class)
    public ResponseDto<Void> handleTransactionException(TransactionException e) {
        return ResponseDto.returnFail(e.getErrorCode(), e.getErrorMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseDto<Void> handleException(Exception e) {
        if (e.getCause() != null && e.getCause() instanceof TransactionException) {
            TransactionException transactionException = (TransactionException) e.getCause();
            return ResponseDto.returnFail(transactionException.getErrorCode(), transactionException.getErrorMessage());
        }

        if (e instanceof BadCredentialsException) {
            return ResponseDto.returnFail(ResponseCodeEnum.LOGIN_PASSWORD_ILLEGAL, e.getMessage());
        }

        if (e instanceof AuthenticationException) {
            return ResponseDto.returnFail(ResponseCodeEnum.LOGIN_ERROR_WITH_MESSAGE, e.getMessage());
        }
        logger.error(e.getMessage(), e);
        return ResponseDto.returnFail(ResponseCodeEnum.UNKOWN_ERROR);
    }
}
