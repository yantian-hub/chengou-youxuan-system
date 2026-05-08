package com.cloud.mail.service.pay.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.mail.api.order.model.Order;
import com.cloud.mail.api.pay.model.PayLog;
import com.cloud.mail.api.pay.model.PayOrder;
import com.cloud.mail.api.pay.model.PayRequest;
import com.cloud.mail.service.pay.service.PayService;
import com.cloud.util.RespResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "支付服务", description = "微信支付集成、支付回调、订单支付状态处理")
@RestController
@RequestMapping("/wx")
@CrossOrigin
public class WeiXinPayController {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private PayService payService;
    @Operation(summary = "支付结果回调", description = "普通订单走事务消息，秒杀订单走秒杀支付Topic")
    //支付结果回调
    @GetMapping("/pay/result/{id}/{status}")
    public String result(@PathVariable String id, @PathVariable int status){
        if(status == 0) return "fail";
        //检查

        // 创建日志对象
        PayLog payLog = new PayLog();
        payLog.setPayId(id);

        // 判断订单类型：秒杀订单ID格式为 userId_goodsId_timestamp
        boolean isSeckill = id.split("_").length >= 3;

        if (isSeckill) {
            // 秒杀订单发送MQ
            rocketMQTemplate.convertAndSend("seckill-pay-topic", id);
        } else {
            // 普通订单发送事务消息
            Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(payLog)).build();
            rocketMQTemplate.sendMessageInTransaction("pay-topic", message, null);
        }

        return "success";
    }



    @Operation(summary = "创建支付订单", description = "生成支付二维码链接")
    @RequestMapping("/pay/creat/{id}")
    public RespResult<String> create(@PathVariable String id){
        //获取订单真实信息
        String result = stringRedisTemplate.opsForValue().get("order:"+id);
        Order order = JSON.parseObject(result, Order.class);
        //构建payrequest
        PayRequest payRequest = new PayRequest();
        payRequest.setOrderId(order.getId());
        payRequest.setAmount(order.getMoney());
        payRequest.setDescription("支付订单");
        payRequest.setUserId(order.getUserId());
        //生成支付订单
        PayOrder payOrder =payService.createMockOrder(payRequest);
        //取出二维码地址返回前端
        return RespResult.ok(payOrder.getCodeUrl());
    }
}
