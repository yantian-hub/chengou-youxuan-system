package com.cloud.mail.api.pay.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统订单号
     */
    private String orderId;

    /**
     * 支付金额（分）
     */
    private Integer amount;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 用户ID
     */
    private String userId;
}
