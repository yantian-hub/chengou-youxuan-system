
package com.cloud.mail.api.pay.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PayCallback implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统订单号
     */
    private String orderId;

    /**
     * 微信支付交易号
     */
    private String transactionId;

    /**
     * 支付状态：1-成功 2-失败
     */
    private Integer status;

    /**
     * 支付金额（分）
     */
    private Integer amount;

    /**
     * 支付完成时间
     */
    private Date payTime;

    /**
     * 回调消息
     */
    private String message;
}
