package com.cloud.mail.service.seckill.mq;

import com.cloud.mail.service.seckill.mapper.SeckillOrderMapper;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill-pay-topic", consumerGroup = "seckill-pay-consumer")
public class SeckillPayListener implements RocketMQListener<String> {

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    @Transactional
    public void onMessage(String orderId) {
        String payKey = "seckill:pay:" + orderId;

        String luaScript =
            "if redis.call('exists', KEYS[1]) == 1 then\n" +
            "    return 0\n" +
            "else\n" +
            "    redis.call('setex', KEYS[1], ARGV[1], ARGV[2])\n" +
            "    return 1\n" +
            "end";

        Long result = stringRedisTemplate.execute(
            new org.springframework.data.redis.core.script.DefaultRedisScript<>(luaScript, Long.class),
            java.util.Collections.singletonList(payKey),
            String.valueOf(24 * 3600),
            "paid"
        );

        if (result == 0) {
            System.out.println("秒杀订单 " + orderId + " 已处理，跳过重复消息");
            log.info("秒杀订单 {} 已处理，跳过重复消息", orderId);
            return;
        }

        try {
            seckillOrderMapper.updateStatus(orderId, 1);
            System.out.println("秒杀订单 " + orderId + " 支付处理成功");
            log.info("秒杀订单 {} 支付处理成功", orderId);
        } catch (Exception e) {
            stringRedisTemplate.delete(payKey);
            e.printStackTrace();
            throw new RuntimeException("秒杀订单支付处理失败，触发重试");
        }
    }


}