package org.mengyun.tcctransaction.server.vo;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Created by cheng.zeng on 2016/9/2.
 */
public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = -6239886213006326837L;

    private Integer code = HttpStatus.OK.value();

    private String message;

    private T data;

    public CommonResponse() {
    }

    public CommonResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
