package com.cloud.mail.service.pay.mq;

import com.alibaba.fastjson.JSON;
import com.cloud.mail.api.pay.model.PayLog;
import com.cloud.mail.service.pay.service.PayLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;

@Slf4j
@RocketMQTransactionListener
public class TransactionListener implements RocketMQLocalTransactionListener {
    @Resource
    private PayLogService payLogService;
    @Resource
    StringRedisTemplate redisTemplate;

    //向mq发送half消息成功之后，会调用这个方法
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            String payLog = (String) message.getPayload();
            PayLog payLog1 = JSON.parseObject(payLog, PayLog.class);
            payLog1.setStatus(1);
            payLogService.add(payLog1);
            redisTemplate.opsForValue().set("pay:", payLog1.getId());
        } catch (Exception e) {
            log.error("支付本地事务执行失败", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }


    //超时回查
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        try {
            String payLog = (String) message.getPayload();
            PayLog payLog1 = JSON.parseObject(payLog, PayLog.class);
            // 去数据库或 Redis 检查这笔支付日志的状态
            Boolean exists = redisTemplate.hasKey("pay:" + payLog1.getId());
            if (Boolean.TRUE.equals(exists)) {
                return RocketMQLocalTransactionState.COMMIT;
            }
            return RocketMQLocalTransactionState.ROLLBACK;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}

