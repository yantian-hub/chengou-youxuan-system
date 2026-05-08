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

import java.util.Collections;

@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill-cancell-topic", consumerGroup = "seckill-cancel-consumer")
public class SeckillCancelListener implements RocketMQListener<String> {

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

            // 检查是否已支付
            String payKey = "seckill:pay:" + orderId;
            Boolean isPaid = stringRedisTemplate.hasKey(payKey);
            if (Boolean.TRUE.equals(isPaid)) {
                log.info("订单 {} 已支付，无需关单", orderId);
                return;
            }

            // 从MySQL查询订单状态
            SeckillOrder order = seckillOrderMapper.selectById(orderId);
            if (order == null) {
                // 订单未持久化，从Redis读取
                String userId = (String) stringRedisTemplate.opsForHash().get("seckill:order:" + orderId, "userId");
                String goodsId = (String) stringRedisTemplate.opsForHash().get("seckill:order:" + orderId, "goodsId");

                if (userId != null && goodsId != null) {
                    // 回滚库存
                    stringRedisTemplate.opsForValue().increment("seckill:goods:" + goodsId);
                    // 删除用户购买标记
                    stringRedisTemplate.opsForHash().delete("seckill:user:" + userId, goodsId);
                    // 标记订单已取消（不删除，等自然过期）
                    stringRedisTemplate.opsForHash().put("seckill:order:" + orderId, "status", "2");
                    log.info("订单 {} 未持久化，已回滚Redis数据并标记取消", orderId);
                }
                return;
            }

            // 订单已存在但未支付，关单并回滚库存
            if (order.getStatus() == 0) {
                // 更新订单状态为已取消（2表示取消）
                seckillOrderMapper.updateStatus(orderId, 2);

                // 回滚库存
                stringRedisTemplate.opsForValue().increment("seckill:goods:" + order.getSeckillGoodsId());
                // 删除用户购买标记
                stringRedisTemplate.opsForHash().delete("seckill:user:" + order.getUsername(), order.getSeckillGoodsId());
                // 标记Redis订单已取消（不删除，等自然过期）
                stringRedisTemplate.opsForHash().put("seckill:order:" + orderId, "status", "2");

                log.info("订单 {} 超时未支付，已关单并回滚库存", orderId);
            } else {
                log.info("订单 {} 状态为 {}，无需关单", orderId, order.getStatus());
            }


        } catch (Exception e) {
            log.error("订单 {} 关单处理失败，将触发重试", orderId, e);
            throw new RuntimeException("关单处理失败: " + orderId, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
