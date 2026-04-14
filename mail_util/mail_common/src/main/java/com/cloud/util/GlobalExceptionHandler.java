package com.cloud.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespResult<?> handleException(Exception e) {
        log.error("系统异常", e);
        return RespResult.error("系统繁忙，请稍后重试");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public RespResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());
        return RespResult.error(e.getMessage());
    }
}