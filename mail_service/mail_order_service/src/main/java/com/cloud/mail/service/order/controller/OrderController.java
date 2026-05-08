package com.cloud.mail.service.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.mail.api.order.model.Order;
import com.cloud.mail.service.order.service.OrderService;
import com.cloud.util.RespResult;
import com.cloud.util.UserIdHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单服务", description = "订单创建、查询、管理")
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/add")
    public RespResult<String> add(@RequestBody Order order,
                                  @RequestHeader("X-User-Id") String userId) {
        order.setUserId(userId);
        if (order.getId() == null) {
            order.setId(IdWorker.getIdStr());
        }
        Boolean o = orderService.add(order);
        return o ? RespResult.ok(order.getId()) : RespResult.error("添加失败");
    }

    @Operation(summary = "根据ID查询订单")
    @GetMapping("/{id}")
    public RespResult<Order> getById(@Parameter(description = "订单ID") @PathVariable String id) {
        return RespResult.ok(orderService.getById(id));
    }

    @Operation(summary = "查询当前用户订单列表")
    @GetMapping("/list")
    public RespResult<List<Order>> list() {
        Long userId = UserIdHolder.get();
        List<Order> orders = orderService.list(
                new QueryWrapper<Order>().eq("user_id", String.valueOf(userId)).orderByDesc("create_time")
        );
        return RespResult.ok(orders);
    }

    @Operation(summary = "分页查询订单")
    @PostMapping("/search/{page}/{size}")
    public RespResult<Page<Order>> search(@RequestBody Order order,
                                          @Parameter(description = "页码") @PathVariable Integer page,
                                          @Parameter(description = "每页条数") @PathVariable Integer size) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(order.getUserId() != null, "user_id", order.getUserId());
        queryWrapper.eq(order.getOrderStatus() != null, "order_status", order.getOrderStatus());
        queryWrapper.orderByDesc("create_time");
        return RespResult.ok(orderService.page(new Page<>(page, size), queryWrapper));
    }
}
