package com.cloud.mail.service.seckill.mq;

import com.cloud.mail.api.seckill.model.SeckillOrder;
import com.cloud.mail.service.seckill.mapper.SeckillOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill-pay-topic", consumerGroup = "seckill-pay-consumer")
public class SeckillPayListener implements RocketMQListener<String> {

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional
    public void onMessage(String orderId) {
        String lockKey = "seckill:order:" + orderId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            Boolean acquired = lock.tryLock(3, 10, java.util.concurrent.TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("获取订单锁失败，等待MQ重试，订单ID: {}", orderId);
                throw new RuntimeException("获取锁失败，订单ID: " + orderId);
            }

            // 检查订单是否已被延时关单取消
            String orderStatus = (String) stringRedisTemplate.opsForHash().get("seckill:order:" + orderId, "status");
            if ("2".equals(orderStatus)) {
                log.error("订单 {} 已被取消但用户已支付，需人工退款", orderId);
                // TODO: 调用退款接口或记录到退款表
                return;
            }

            String payKey = "seckill:pay:" + orderId;

            Long result = stringRedisTemplate.execute(
                new org.springframework.data.redis.core.script.DefaultRedisScript<>(
                    "if redis.call('exists', KEYS[1]) == 0 then " +
                    "   redis.call('setex', KEYS[1], ARGV[1], ARGV[2]) " +
                    "   return 1 " +
                    "else " +
                    "   return 0 " +
                    "end",
                    Long.class
                ),
                Collections.singletonList(payKey),
                String.valueOf(24 * 3600),
                "paid"
            );

            if (result == 0) {
                log.info("秒杀订单 {} 已处理，跳过重复消息", orderId);
                return;
            }

            // 检查MySQL是否有订单
            if (seckillOrderMapper.selectById(orderId) == null) {
                // 订单不存在，从Redis拉取数据插入数据库
                String userId = (String) stringRedisTemplate.opsForHash().get("seckill:order:" + orderId, "userId");
                String goodsId = (String) stringRedisTemplate.opsForHash().get("seckill:order:" + orderId, "goodsId");

                if (userId == null || goodsId == null) {
                    throw new RuntimeException("Redis中订单数据不存在，订单ID: " + orderId);
                }

                SeckillOrder newOrder = new SeckillOrder();
                newOrder.setId(orderId);
                newOrder.setUsername(userId);
                newOrder.setSeckillGoodsId(goodsId);
                newOrder.setStatus(1); // 直接设为已支付
                newOrder.setCreateTime(LocalDateTime.now());
                newOrder.setPayTime(LocalDateTime.now());

                seckillOrderMapper.insert(newOrder);
                log.info("秒杀订单 {} 补单成功", orderId);
            } else {
                // 订单已存在，只更新支付状态
                seckillOrderMapper.updateStatus(orderId, 1);
            }

            log.info("秒杀订单 {} 支付处理成功", orderId);
        }catch (Exception e) {
            // 回滚支付标记，允许重试
            stringRedisTemplate.delete("seckill:pay:" + orderId);
            log.error("秒杀订单 {} 支付处理失败，将触发重试", orderId, e);
            throw new RuntimeException("秒杀订单支付处理失败: " + orderId, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
