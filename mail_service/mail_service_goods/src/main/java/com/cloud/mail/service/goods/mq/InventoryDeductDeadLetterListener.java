package com.cloud.mail.service.goods.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
    topic = "%DLQ%inventory-deduct-consumer",
    consumerGroup = "dlq-inventory-deduct-consumer"
)
public class InventoryDeductDeadLetterListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String orderJson) {
        log.error("【死信消息】库存扣减失败，需人工介入处理 - order: {}", orderJson);
    }
}