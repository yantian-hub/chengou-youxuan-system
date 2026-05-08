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
        //用户+物品关联锁
        RLock lock = redissonClient.getLock("user:" + usrId+"item:"+seckillGoodsid);
        boolean luaExecuted = false;
        String orderId = null;
        try {
            Boolean b = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!b) return null;//获取锁失败直接返回

            //lua脚本预处理库存
            Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(SECKILL_LUA_SCRIPT, Long.class),
                Arrays.asList(
                    "seckill:goods:" + seckillGoodsid,
                    "seckill:user:" + usrId
                ),
                seckillGoodsid,
                usrId
            );

            luaExecuted = true;

            if (result == -1) {
                return null;
            } else if (result == -2) {
                return null;
            } else if (result == 1) {
                //库存扣减成功，生成订单
                orderId = usrId + "_" + seckillGoodsid + "_" + System.currentTimeMillis();

                //写redis
                stringRedisTemplate.opsForHash().put("seckill:order:" + orderId, "userId", usrId);
                stringRedisTemplate.opsForHash().put("seckill:order:" + orderId, "goodsId", seckillGoodsid);
                //订单状态设为未支付
                stringRedisTemplate.opsForHash().put("seckill:order:" + orderId, "status", "0");
                stringRedisTemplate.expire("seckill:order:" + orderId, 24, TimeUnit.HOURS);

                //mq异步写db
                rocketMQTemplate.convertAndSend("seckill-order-topic", orderId);

                //mq发送延时消息,用于库存回滚（延迟15分钟）
                rocketMQTemplate.syncSend("seckill-cancell-topic",
                        org.springframework.messaging.support.MessageBuilder.withPayload(orderId).build(),
                        3000,  // 超时时间
                        4      // 延迟级别：4表示15分钟
                );
                return orderId;
            }

            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // 只有Lua脚本执行成功后才需要回滚
            if (luaExecuted) {
                stringRedisTemplate.opsForValue().increment("seckill:goods:" + seckillGoodsid);
                stringRedisTemplate.opsForHash().delete("seckill:user:" + usrId, seckillGoodsid);
            }
            return null;
        } catch (Exception e) {
            // 其他异常（Redis/MQ失败），回滚所有数据
            if (luaExecuted) {
                stringRedisTemplate.opsForValue().increment("seckill:goods:" + seckillGoodsid);
                stringRedisTemplate.opsForHash().delete("seckill:user:" + usrId, seckillGoodsid);
                if (orderId != null) {
                    stringRedisTemplate.delete("seckill:order:" + orderId);
                }
            }
            throw new RuntimeException("秒杀处理失败", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


}

