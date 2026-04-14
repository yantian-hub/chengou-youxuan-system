package com.cloud.mail.service.order.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.mail.api.cart.feign.Cartfeign;
import com.cloud.mail.api.cart.model.Cart;
import com.cloud.mail.api.order.model.Order;
import com.cloud.mail.api.order.model.Ordersku;
import com.cloud.mail.service.goods.feign.Skufeign;
import com.cloud.mail.service.order.Mapper.OrderMapper;
import com.cloud.mail.service.order.Mapper.OrderSkuMapper;
import com.cloud.mail.service.order.service.OrderService;
import com.cloud.util.RespResult;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderSkuMapper orderskuMapper;

    @Resource
    private Skufeign skufeign;

    @Resource
    private Cartfeign cartfeign;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

   // ... existing code ...

    @Override
    public Boolean add(Order order) {
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());

        RespResult<List<Cart>> cartResp = cartfeign.list();
        List<Cart> carts = cartResp.getData();
        if(carts==null || carts.isEmpty()){
            return false;
        }

        int totalNum = 0;
        int totalMoney = 0;
        for(Cart cart:carts){
            Ordersku orderSku = JSON.parseObject(cart.toString(), Ordersku.class);
            orderSku.setOrderId(order.getId());
            orderSku.setId(IdWorker.getIdStr());
            orderSku.setMoney(orderSku.getPrice()*orderSku.getNum());
            orderskuMapper.insert(orderSku);
            totalNum += orderSku.getNum();
            totalMoney += orderSku.getMoney();

            stringRedisTemplate.opsForList().rightPush("order:items:" + order.getId(), JSON.toJSONString(orderSku));
        }

        order.setTotalNum(totalNum);
        order.setMoney(totalMoney);

        String orderJson = JSON.toJSONString(order);
        Message<String> message = MessageBuilder.withPayload(orderJson).build();

        rocketMQTemplate.sendMessageInTransaction("order-create-topic", message, null);

        stringRedisTemplate.opsForValue().set("order:"+order.getId(), orderJson);
        stringRedisTemplate.expire("order:items:" + order.getId(), 24, java.util.concurrent.TimeUnit.HOURS);

        return true;
    }
}


