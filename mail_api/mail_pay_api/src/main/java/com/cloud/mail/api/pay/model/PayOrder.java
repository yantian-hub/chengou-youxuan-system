package com.cloud.mail.api.pay.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PayOrder implements Serializable {

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
     * 支付状态：0-待支付 1-已支付 2-支付失败 3-已关闭
     */
    private Integer status;

    /**
     * 微信支付交易号
     */
    private String transactionId;

    /**
     * 二维码链接（Native支付）
     */
    private String codeUrl;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 过期时间
     */
    private Date expireTime;
}
