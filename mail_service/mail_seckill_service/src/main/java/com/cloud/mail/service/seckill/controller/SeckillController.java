package com.cloud.mail.service.seckill.controller;

import com.cloud.mail.api.seckill.model.SeckillOrder;
import com.cloud.mail.service.seckill.mapper.SeckillOrderMapper;
import com.cloud.mail.service.seckill.service.SeckilladdService;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Tag(name = "秒杀服务", description = "高并发秒杀核心接口 — Redis+Lua原子扣库存 + RocketMQ异步落库")
@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Resource
    private SeckilladdService seckillAddService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Resource
    private MeterRegistry meterRegistry;

    private Counter seckillSuccessCounter;
    private Counter seckillFailCounter;

    @PostConstruct
    public void init() {
        seckillSuccessCounter = Counter.builder("seckill.success")
            .description("秒杀成功次数")
            .register(meterRegistry);

        seckillFailCounter = Counter.builder("seckill.fail")
            .description("秒杀失败次数")
            .register(meterRegistry);
    }

    @Operation(summary = "秒杀下单", description = "Redis+Lua原子扣库存，RocketMQ异步落库，Redisson分布式锁防超卖")
    @PostMapping("/doSeckill/{goodsId}")
    public RespResult<String> doSeckill(@Parameter(description = "秒杀商品ID") @PathVariable String goodsId) {
        Long userId = UserIdHolder.get();
        String orderId = seckillAddService.add(goodsId, String.valueOf(userId));
        if (orderId != null) {
            seckillSuccessCounter.increment();
            return RespResult.ok(orderId);
        } else {
            seckillFailCounter.increment();
            return RespResult.error("秒杀失败，库存不足或已购买过");
        }
    }

    @Operation(summary = "查询秒杀订单", description = "根据订单ID查询秒杀订单详情")
    @GetMapping("/queryOrder/{orderId}")
    public RespResult<SeckillOrder> queryOrder(@Parameter(description = "订单ID") @PathVariable String orderId) {
        SeckillOrder order = seckillOrderMapper.selectById(orderId);
        if (order != null) {
            return RespResult.ok(order);
        } else {
            return RespResult.error("订单不存在");
        }
    }
}
