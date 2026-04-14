package com.cloud.mail.service.seckill.mq;

import com.cloud.mail.api.seckill.model.SeckillOrder;
import com.cloud.mail.service.seckill.mapper.SeckillOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill-order-topic", consumerGroup = "seckill-order-consumer")
public class SeckillOrderListener implements RocketMQListener<String> {

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public void onMessage(String orderId) {
        String orderKey = "Seckill:Order:processed:" + orderId;

        String luaScript =
            "if redis.call('exists', KEYS[1]) == 1 then\n" +
            "    return 0\n" +
            "else\n" +
            "    redis.call('setex', KEYS[1], ARGV[1], ARGV[2])\n" +
            "    return 1\n" +
            "end";

        Long result = stringRedisTemplate.execute(
            new DefaultRedisScript<>(luaScript, Long.class),
            java.util.Collections.singletonList(orderKey),
            String.valueOf(24 * 3600),
            "1"
        );

        if (result == 0) {
            log.info("秒杀订单 {} 已处理，跳过重复消息", orderId);
            return;
        }

        try {
            String[] parts = orderId.split("_");
            String userId = parts[0];
            String goodsId = parts[1];

            SeckillOrder order = new SeckillOrder();
            order.setId(orderId);
            order.setSeckillGoodsId(goodsId);
            order.setUsername(userId);
            order.setStatus(0);
            order.setCreateTime(LocalDateTime.now());
            seckillOrderMapper.insert(order);

            log.info("秒杀订单 {} 创建成功", orderId);
        } catch (Exception e) {
            log.error("秒杀订单创建失败，触发重试 - orderId: {}", orderId, e);
            throw new RuntimeException("秒杀订单创建失败，触发重试");
        }
    }

}
