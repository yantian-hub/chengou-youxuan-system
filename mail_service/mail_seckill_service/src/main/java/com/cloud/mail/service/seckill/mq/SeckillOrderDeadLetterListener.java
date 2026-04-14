package com.cloud.mail.service.seckill.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
    topic = "%DLQ%seckill-order-consumer",
    consumerGroup = "dlq-seckill-order-consumer"
)
public class SeckillOrderDeadLetterListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String orderId) {
        log.error("【死信消息】秒杀订单创建失败，需人工介入处理 - orderId: {}", orderId);
    }
}