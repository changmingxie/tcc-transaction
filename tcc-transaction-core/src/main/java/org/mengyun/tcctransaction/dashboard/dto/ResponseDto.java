package org.mengyun.tcctransaction.dashboard.dto;

import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;

/**
 * @Author huabao.fang
 * @Date 2021/2/20 17:18
 **/
public class ResponseDto<T> {

    private String code;
    private String message;
    private T data;

    public ResponseDto() {
    }

    public ResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseDto<Void> returnSuccess() {
        return returnResponse(ResponseCodeEnum.SUCCESS, null);
    }

    public static <T> ResponseDto<T> returnSuccess(T data) {
        return returnResponse(ResponseCodeEnum.SUCCESS, data);
    }

    public static <T> ResponseDto<T> returnFail(ResponseCodeEnum responseCode) {
        return returnResponse(responseCode, null);
    }

    public static <T> ResponseDto<T> returnFail(String code, String message) {
        return new ResponseDto<>(code, message);
    }

    public static <T> ResponseDto<T> returnFail(ResponseCodeEnum responseCode, String customMessage) {
        return new ResponseDto<>(responseCode.getCode(), responseCode.getResponseMessage(customMessage), null);
    }

    public static <T> ResponseDto<T> returnResponse(ResponseCodeEnum responseCode, T data) {
        return new ResponseDto<>(responseCode.getCode(), responseCode.getMessage(), data);
    }

    public boolean isSuccess() {
        return ResponseCodeEnum.SUCCESS.getCode().equals(this.code);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getCodeAndMessage() {
        return this.code + "-" + this.message;
    }

}
