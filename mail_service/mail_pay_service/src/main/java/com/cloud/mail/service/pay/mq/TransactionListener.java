package com.cloud.mail.service.pay.mq;

import com.alibaba.fastjson.JSON;
import com.cloud.mail.api.pay.model.PayLog;
import com.cloud.mail.service.pay.service.PayLogService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;

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

            // 使用payId（订单ID）作为key，它在insert前就有值
            redisTemplate.opsForValue().set("pay:" + payLog1.getPayId(), "1", 7, java.util.concurrent.TimeUnit.DAYS);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }


    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        try {
            String payLog = (String) message.getPayload();
            PayLog payLog1 = JSON.parseObject(payLog, PayLog.class);
            // 使用payId回查
            Boolean exists = redisTemplate.hasKey("pay:" + payLog1.getPayId());
            if (Boolean.TRUE.equals(exists)) {
                return RocketMQLocalTransactionState.COMMIT;
            }
            return RocketMQLocalTransactionState.ROLLBACK;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

}

