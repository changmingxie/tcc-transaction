package org.mengyun.tcctransaction.server.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Created by cheng.zeng on 2016/9/2.
 */
@Data
public class Result<T> implements Serializable {

    private int code = HttpStatus.OK.value();
    private String message;
    private T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "OK", data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(200, "OK", null);
    }

    public static <T> Result<T> err(String err) {
        return new Result<>(-1, err, null);
    }

    public static <T> Result<T> err(Throwable err) {
        return new Result<>(-1, err.getMessage(), null);
    }
}
