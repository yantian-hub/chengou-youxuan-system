package com.cloud.mail.service.seckill.service.Impl;

import com.cloud.mail.service.seckill.service.SeckilladdService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillAddServiceImpl implements SeckilladdService {

    private static final String SECKILL_LUA_SCRIPT =
        "local stockKey = KEYS[1]\n" +
        "local userKey = KEYS[2]\n" +
        "local goodsId = ARGV[1]\n" +
        "local userId = ARGV[2]\n" +
        "\n" +
        "-- 1. 校验库存\n" +
        "local stock = tonumber(redis.call('get', stockKey))\n" +
        "if not stock or stock <= 0 then\n" +
        "    return -1\n" +
        "end\n" +
        "\n" +
        "-- 2. 校验一人一单\n" +
        "if redis.call('hexists', userKey, goodsId) == 1 then\n" +
        "    return -2\n" +
        "end\n" +
        "\n" +
        "-- 3. 扣减库存\n" +
        "redis.call('decr', stockKey)\n" +
        "\n" +
        "-- 4. 标记用户已购买\n" +
        "redis.call('hset', userKey, goodsId, '1')\n" +
        "\n" +
        "return 1";

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RedissonClient redissonClient;
    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public String add(String seckillGoodsid, String usrId) {
        RLock lock = redissonClient.getLock("seckill:lock:" + usrId);
        try {
            Boolean b = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!b) return null;

            Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(SECKILL_LUA_SCRIPT, Long.class),
                Arrays.asList(
                    "Seckill:Goods:" + seckillGoodsid,
                    "Seckill:User:" + usrId
                ),
                seckillGoodsid,
                usrId
            );

            if (result == -1) {
                return null;
            } else if (result == -2) {
                return null;
            } else if (result == 1) {
                String orderId = usrId + "_" + seckillGoodsid + "_" + System.currentTimeMillis();

                stringRedisTemplate.opsForHash().put("Seckill:Order:" + orderId, "userId", usrId);
                stringRedisTemplate.opsForHash().put("Seckill:Order:" + orderId, "goodsId", seckillGoodsid);
                stringRedisTemplate.opsForHash().put("Seckill:Order:" + orderId, "status", "0");
                stringRedisTemplate.expire("Seckill:Order:" + orderId, 24, TimeUnit.HOURS);

                rocketMQTemplate.convertAndSend("seckill-order-topic", orderId);

                return orderId;
            }

            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}

