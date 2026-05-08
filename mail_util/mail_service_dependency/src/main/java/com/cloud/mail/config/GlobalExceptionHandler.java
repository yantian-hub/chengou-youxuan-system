package com.cloud.mail.config;

import com.cloud.util.RespCode;
import com.cloud.util.RespResult;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public RespResult<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return RespResult.error(RespCode.PARAM_ERROR.getCode(), "缺少必要参数: " + e.getParameterName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public RespResult<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        return RespResult.error(RespCode.PARAM_ERROR.getCode(), "请求参数格式错误");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public RespResult<Void> handleIllegalArgument(IllegalArgumentException e) {
        return RespResult.error(RespCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public RespResult<Void> handleFeignException(FeignException e) {
        log.error("Feign调用异常: {}", e.getMessage(), e);
        return RespResult.error(RespCode.SERVICE_UNAVAILABLE.getCode(), "服务调用失败，请稍后重试");
    }

    @ExceptionHandler(RuntimeException.class)
    public RespResult<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return RespResult.error(RespCode.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public RespResult<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return RespResult.error(RespCode.SYSTEM_ERROR);
    }
}
