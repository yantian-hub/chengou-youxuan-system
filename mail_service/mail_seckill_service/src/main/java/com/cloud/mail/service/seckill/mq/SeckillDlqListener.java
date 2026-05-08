package com.cloud.mail.service.seckill.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
    topic = "%DLQ%seckill-cancel-consumer",
    consumerGroup = "seckill-dlq-consumer"
)
public class SeckillDlqListener implements RocketMQListener<String> {

    private static final Logger dlqLogger = LoggerFactory.getLogger("DLQ_LOGGER");

    @Override
    public void onMessage(String orderId) {
        dlqLogger.error("死信消息 | 订单ID: {} | 时间: {} | 原因: 关单重试多次失败，需人工介入",
            orderId,
            java.time.LocalDateTime.now()
        );

        log.error("订单 {} 关单失败多次，已转入死信队列", orderId);
    }
}
