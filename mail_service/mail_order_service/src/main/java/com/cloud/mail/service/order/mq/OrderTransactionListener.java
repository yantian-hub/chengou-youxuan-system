package com.cloud.mail.service.order.mq;

import com.alibaba.fastjson2.JSON;
import com.cloud.mail.api.order.model.Order;
import com.cloud.mail.service.order.Mapper.OrderMapper;
import com.cloud.mail.service.order.Mapper.OrderSkuMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@RocketMQTransactionListener
@Component
public class OrderTransactionListener implements RocketMQLocalTransactionListener {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderSkuMapper orderSkuMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        try {
            String orderJson = new String((byte[]) message.getPayload());
            Order order = JSON.parseObject(orderJson, Order.class);

            String orderId = order.getId();
            String orderKey = "order:tx:" + orderId;

            Boolean exists = stringRedisTemplate.hasKey(orderKey);
            if (Boolean.TRUE.equals(exists)) {
                log.info("订单 {} 已存在，直接提交", orderId);
                return RocketMQLocalTransactionState.COMMIT;
            }

            int orderCount = orderMapper.insert(order);
            if (orderCount > 0) {
                stringRedisTemplate.opsForValue().set(orderKey, "created", 24, TimeUnit.HOURS);
                log.info("订单 {} 创建成功，提交事务", orderId);
                return RocketMQLocalTransactionState.COMMIT;
            } else {
                log.warn("订单 {} 创建失败，回滚事务", orderId);
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        } catch (Exception e) {
            log.error("订单本地事务执行失败", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        try {
            String orderJson = new String((byte[]) message.getPayload());
            Order order = JSON.parseObject(orderJson, Order.class);

            String orderKey = "order:tx:" + order.getId();
            Boolean exists = stringRedisTemplate.hasKey(orderKey);

            if (Boolean.TRUE.equals(exists)) {
                log.info("回查订单 {} 已创建，提交消息", order.getId());
                return RocketMQLocalTransactionState.COMMIT;
            }

            log.warn("回查订单 {} 未找到，回滚消息", order.getId());
            return RocketMQLocalTransactionState.ROLLBACK;
        } catch (Exception e) {
            log.error("回查本地事务状态异常", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
