package com.cloud.mail.service.order.mq;


import com.cloud.mail.service.order.Mapper.OrderMapper;
import com.cloud.mail.service.order.service.OrderService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
    topic = "pay-topic",
    consumerGroup = "order-pay-consumer-group"
)
public class OrderResultListener implements RocketMQListener<String> {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void onMessage(String orderId) {
        String payKey = "order:pay:" + orderId;
        Boolean hasPaid = stringRedisTemplate.hasKey(payKey);

        if (Boolean.TRUE.equals(hasPaid)) {
            System.out.println("订单 " + orderId + " 已处理，跳过重复消息");
            log.info("订单 {} 已处理，跳过重复消息", orderId);
            return;
        }

        try {
            orderMapper.updatePayStatus(orderId, 2);

            stringRedisTemplate.opsForValue().set(payKey, "paid", 24, java.util.concurrent.TimeUnit.HOURS);

            System.out.println("订单 " + orderId + " 支付处理成功");
            log.info("订单 {} 支付处理成功", orderId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("订单支付处理失败，触发重试");
        }
    }
}
