package com.cloud.mail.service.pay.service;

import com.cloud.mail.api.pay.model.PayOrder;
import com.cloud.mail.api.pay.model.PayRequest;

public interface PayService {

    /**
     * 创建模拟支付订单
     */
    PayOrder createMockOrder(PayRequest request);

    /**
     * 处理支付成功回调
     */
    void handlePaySuccess(String orderId);

    /**
     * 处理支付失败回调
     */
    void handlePayFail(String orderId);
}
