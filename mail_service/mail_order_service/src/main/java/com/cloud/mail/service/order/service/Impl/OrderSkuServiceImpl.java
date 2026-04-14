package com.cloud.mail.service.order.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.api.order.model.Ordersku;
import com.cloud.mail.service.order.Mapper.OrderSkuMapper;
import com.cloud.mail.service.order.service.OrderSkuService;
import org.springframework.stereotype.Service;

@Service
public class OrderSkuServiceImpl extends ServiceImpl<OrderSkuMapper, Ordersku>
    implements OrderSkuService {
}
