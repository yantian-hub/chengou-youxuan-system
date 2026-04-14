package com.cloud.mail.service.goods.mq;

import com.alibaba.fastjson2.JSON;
import com.cloud.mail.api.order.model.Order;
import com.cloud.mail.api.order.model.Ordersku;
import com.cloud.mail.service.goods.mapper.SkuMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RocketMQMessageListener(topic = "order-create-topic", consumerGroup = "inventory-deduct-consumer")
public class InventoryDeductListener implements RocketMQListener<String> {

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(String orderJson) {
        try {
            Order order = JSON.parseObject(orderJson, Order.class);
            String orderId = order.getId();

            // 检查是否已处理过该订单
            String deductKey = "inventory:deduct:" + orderId;
            Boolean exists = stringRedisTemplate.hasKey(deductKey);
            if (Boolean.TRUE.equals(exists)) {
                log.info("订单 {} 库存已扣减，跳过重复消息", orderId);
                return;
            }

            // 获取订单项
            String orderItemsKey = "order:items:" + orderId;
            List<Object> items = stringRedisTemplate.opsForList().range(orderItemsKey, 0, -1);

            if (items != null && !items.isEmpty()) {
                for (Object item : items) {
                    Ordersku orderSku = JSON.parseObject(item.toString(), Ordersku.class);
                    int result = skuMapper.docunt(orderSku.getSkuId(), orderSku.getNum());
                    if (result == 0) {
                        throw new RuntimeException("库存不足: " + orderSku.getSkuId());
                    }
                }
            }
            // 标记该订单已处理
            stringRedisTemplate.opsForValue().set(deductKey, "deducted", 24, TimeUnit.HOURS);
            log.info("订单 {} 库存扣减成功", orderId);

        } catch (Exception e) {
            log.error("库存扣减失败，触发重试: {}", e.getMessage(), e);
            throw new RuntimeException("库存扣减失败，触发重试: " + e.getMessage());
        }
    }
}
