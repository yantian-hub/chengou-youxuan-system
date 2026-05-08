package com.cloud.mail.service.seckill.mq;

import com.cloud.mail.api.seckill.model.SeckillOrder;
import com.cloud.mail.service.seckill.mapper.SeckillOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        try {
            //查询mysql中订单信息是否存在
            SeckillOrder seckillOrder = seckillOrderMapper.selectById(orderId);

            //订单存在幂等丢弃
            if (seckillOrder != null) {
                log.info("秒杀订单 {} 已处理，跳过重复消息", orderId);
                return;
            }

            // 从Redis中获取订单信息
            String userId = (String) stringRedisTemplate.opsForHash().get("seckill:order:" + orderId, "userId");
            String goodsId = (String) stringRedisTemplate.opsForHash().get("seckill:order:" + orderId, "goodsId");

            if (userId == null || goodsId == null) {
                throw new RuntimeException("Redis中订单数据不存在，订单ID: " + orderId);
            }

            //订单不存在，mysql写入数据，订单状态设为0，未支付
            SeckillOrder newOrder = new SeckillOrder();
            newOrder.setId(orderId);
            newOrder.setUsername(userId);
            newOrder.setSeckillGoodsId(goodsId);
            newOrder.setStatus(0);
            newOrder.setCreateTime(LocalDateTime.now());

            seckillOrderMapper.insert(newOrder);

            log.info("秒杀订单 {} 持久化成功", orderId);
        } catch (Exception e) {
            log.error("秒杀订单 {} 消费失败，将触发重试", orderId, e);
            throw new RuntimeException("秒杀订单消费失败: " + orderId, e);
        }
    }

}
