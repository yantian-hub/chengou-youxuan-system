package com.cloud.mail.service.pay.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.mail.api.pay.model.PayCallback;
import com.cloud.mail.api.pay.model.PayOrder;
import com.cloud.mail.api.pay.model.PayRequest;
import com.cloud.mail.api.pay.model.PayLog;
import com.cloud.mail.service.pay.mapper.PayLogMapper;
import com.cloud.mail.service.pay.service.PayService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public PayOrder createMockOrder(PayRequest request) {
        // 1. 生成支付订单
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderId(request.getOrderId());
        payOrder.setAmount(request.getAmount());
        payOrder.setStatus(0); // 待支付
        payOrder.setCreateTime(new Date());

        // 2. 保存支付记录
        PayLog payLog = new PayLog();
        payLog.setPayId(request.getOrderId());
        payLog.setStatus(0);
        payLog.setContent("模拟支付订单");
        payLog.setCreateTime(new Date());
        payLogMapper.insert(payLog);

        // 3. 返回模拟的二维码链接
        //把payorder给微信
        //微信给code_url封装到payorder
        payOrder.setCodeUrl("https://mock.wechat.pay/qrcode/" + request.getOrderId());
        //payorder给前端
        return payOrder;
    }

    @Transactional
    @Override
    public void handlePaySuccess(String orderId) {
        // 1. 更新支付记录
        PayLog payLog = payLogMapper.selectOne(
            new LambdaQueryWrapper<PayLog>().eq(PayLog::getPayId, orderId)
        );
        if (payLog != null) {
            payLog.setStatus(1); // 支付成功
            payLog.setContent("模拟支付成功");
            payLogMapper.updateById(payLog);
        }

        // 2. 发送MQ消息通知订单服务
        PayCallback callback = new PayCallback();
        callback.setOrderId(orderId);
        callback.setTransactionId("MOCK_" + System.currentTimeMillis());
        callback.setStatus(1);

        rocketMQTemplate.convertAndSend("pay-success-topic", callback);

        System.out.println("✅ 模拟支付成功: " + orderId);
    }

    @Override
    public void handlePayFail(String orderId) {
        PayLog payLog = payLogMapper.selectOne(
            new LambdaQueryWrapper<PayLog>().eq(PayLog::getPayId, orderId)
        );
        if (payLog != null) {
            payLog.setStatus(2); // 支付失败
            payLog.setContent("模拟支付失败");
            payLogMapper.updateById(payLog);
        }

        System.out.println("❌ 模拟支付失败: " + orderId);
    }
}
