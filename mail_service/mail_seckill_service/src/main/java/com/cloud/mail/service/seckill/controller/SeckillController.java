package com.cloud.mail.service.seckill.controller;

import com.cloud.mail.api.seckill.model.SeckillOrder;
import com.cloud.mail.service.seckill.mapper.SeckillOrderMapper;
import com.cloud.mail.service.seckill.service.SeckilladdService;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Resource
    private SeckilladdService seckillAddService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SeckillOrderMapper seckillOrderMapper;


    @PostMapping("/doSeckill/{goodsId}")
    public RespResult<String> doSeckill(@PathVariable String goodsId) {
        Long userId = UserIdHolder.get();
        String orderId = seckillAddService.add(goodsId, String.valueOf(userId));
        if (orderId != null) {
            return RespResult.ok(orderId);
        } else {
            return RespResult.error("秒杀失败，库存不足或已购买过");
        }
    }



    @GetMapping("/queryOrder/{orderId}")
    public RespResult<SeckillOrder> queryOrder(@PathVariable String orderId) {
        Object userId = stringRedisTemplate.opsForHash().get("Seckill:Order:" + orderId, "userId");
        Object goodsId = stringRedisTemplate.opsForHash().get("Seckill:Order:" + orderId, "goodsId");

        if (userId != null && goodsId != null) {
            SeckillOrder order = new SeckillOrder();
            order.setId(orderId);
            order.setUsername(userId.toString());
            order.setSeckillGoodsId(goodsId.toString());
            return RespResult.ok(order);
        }

        SeckillOrder order = seckillOrderMapper.selectById(orderId);
        if (order == null) {
            return RespResult.error("订单不存在");
        }

        return RespResult.ok(order);
    }
}
