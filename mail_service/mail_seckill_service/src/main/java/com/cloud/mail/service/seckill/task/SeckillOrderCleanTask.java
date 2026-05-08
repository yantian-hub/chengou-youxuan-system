package com.cloud.mail.service.seckill.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.mail.api.seckill.model.SeckillOrder;
import com.cloud.mail.service.seckill.mapper.SeckillOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SeckillOrderCleanTask {

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanCancelledOrders() {
        int count = seckillOrderMapper.delete(
            new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getStatus, 2)
                .lt(SeckillOrder::getCreateTime, LocalDateTime.now().minusDays(7))
        );
        log.info("清理已取消秒杀订单 {} 条", count);
    }
}
