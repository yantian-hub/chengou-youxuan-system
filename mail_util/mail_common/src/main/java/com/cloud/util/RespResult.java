package com.cloud.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T data;
    private Integer code;
    private String message;

    public static <T> RespResult<T> ok() {
        RespResult<T> result = new RespResult<>();
        result.setCode(RespCode.SUCCESS.getCode());
        result.setMessage(RespCode.SUCCESS.getMessage());
        return result;
    }

    public static <T> RespResult<T> ok(String message) {
        RespResult<T> result = new RespResult<>();
        result.setCode(RespCode.SUCCESS.getCode());
        result.setMessage(message);
        return result;
    }

    public static <T> RespResult<T> ok(T data) {
        RespResult<T> result = new RespResult<>();
        result.setCode(RespCode.SUCCESS.getCode());
        result.setMessage(RespCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> RespResult<T> ok(String message, T data) {
        RespResult<T> result = new RespResult<>();
        result.setCode(RespCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> RespResult<T> error() {
        RespResult<T> result = new RespResult<>();
        result.setCode(RespCode.ERROR.getCode());
        result.setMessage(RespCode.ERROR.getMessage());
        return result;
    }

    public static <T> RespResult<T> error(String message) {
        RespResult<T> result = new RespResult<>();
        result.setCode(RespCode.ERROR.getCode());
        result.setMessage(message);
        return result;
    }

    public static <T> RespResult<T> error(Integer code, String message) {
        RespResult<T> result = new RespResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> RespResult<T> error(RespCode respCode) {
        RespResult<T> result = new RespResult<>();
        result.setCode(respCode.getCode());
        result.setMessage(respCode.getMessage());
        return result;
    }
}
