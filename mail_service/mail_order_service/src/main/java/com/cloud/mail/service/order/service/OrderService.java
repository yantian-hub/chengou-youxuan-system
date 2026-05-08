package com.cloud.mail.service.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.mail.api.order.model.Order;

public interface OrderService extends IService<Order> {

    //添加订单
    Boolean add(Order order);
}
