package com.cloud.mail.service.order.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.cloud.mail.api.order.model.Order;
import com.cloud.mail.service.order.service.OrderService;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping("/add")
    public RespResult<String> add(@RequestBody Order order,
                              @RequestHeader("X-User-Id") String userId) {
        order.setUserId(userId);
        order.setId(IdWorker.getIdStr());
        Boolean result = orderService.add(order);
        return result ? RespResult.ok(order.getId()) : RespResult.error("添加失败");
    }
}
