package com.cloud.mail.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Configuration
public class SentinelGatewayConfig {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public SentinelGatewayConfig(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                 ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void doInit() {
        initGatewayRules();
    }

    /**
     * 初始化网关限流规则
     * 针对不同服务路由设置不同的QPS阈值
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();

        // 秒杀接口限流：QPS=100，防止高并发冲击后端服务
        GatewayFlowRule seckillRule = new GatewayFlowRule("mail-service-seckill-route")
                .setCount(100)
                .setIntervalSec(1);
        rules.add(seckillRule);

        // 订单接口限流：QPS=50，订单创建涉及数据库写操作，限制更严格
        GatewayFlowRule orderRule = new GatewayFlowRule("mail-service-order-route")
                .setCount(50)
                .setIntervalSec(1);
        rules.add(orderRule);

        // 购物车接口限流：QPS=200，购物车操作多为Redis读写，可承受更高并发
        GatewayFlowRule cartRule = new GatewayFlowRule("mail-service-cart-route")
                .setCount(200)
                .setIntervalSec(1);
        rules.add(cartRule);

        // 用户服务限流：QPS=100，登录注册不需要太严格
        GatewayFlowRule usrRule = new GatewayFlowRule("mail-service-usr-route")
                .setCount(100)
                .setIntervalSec(1);
        rules.add(usrRule);

        // 支付服务限流：QPS=80，支付接口需保证稳定性
        GatewayFlowRule payRule = new GatewayFlowRule("mail-service-pay-route")
                .setCount(80)
                .setIntervalSec(1);
        rules.add(payRule);

        // 商品服务限流：QPS=300，商品查询多为缓存读取
        GatewayFlowRule goodsRule = new GatewayFlowRule("mail-service-goods-route")
                .setCount(300)
                .setIntervalSec(1);
        rules.add(goodsRule);

        // 加载限流规则到Sentinel
        com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager.loadRules(rules);
    }

}
