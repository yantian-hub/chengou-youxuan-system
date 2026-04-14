package com.cloud.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RespCode {
    SUCCESS(20000, "操作成功"),

    ERROR(50000, "操作失败"),
    SYSTEM_ERROR(50001, "系统异常"),
    SERVICE_UNAVAILABLE(50002, "服务不可用"),

    PARAM_ERROR(40000, "参数错误"),
    BUSINESS_ERROR(40001, "业务异常"),
    DATA_NOT_FOUND(40002, "数据不存在"),
    DUPLICATE_DATA(40003, "数据重复"),
    VALIDATION_ERROR(40004, "验证失败"),

    UNAUTHORIZED(40100, "未授权"),
    TOKEN_EXPIRED(40101, "令牌过期"),
    TOKEN_INVALID(40102, "令牌无效"),
    FORBIDDEN(40300, "禁止访问"),

    NOT_FOUND(40400, "资源不存在"),

    STOCK_INSUFFICIENT(41000, "库存不足"),
    ORDER_CREATE_FAILED(41001, "订单创建失败"),
    ORDER_NOT_FOUND(41002, "订单不存在"),
    PAYMENT_FAILED(41003, "支付失败");

    private final Integer code;
    private final String message;
}
