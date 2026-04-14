package com.cloud.mail.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
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
     * 初始化网关热点参数限流规则
     * 基于用户ID限制单用户访问频率，防止恶意刷接口
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();

        // 秒杀接口热点参数限流：每个用户ID QPS=5，防止单用户刷接口
        GatewayFlowRule seckillUserRule = new GatewayFlowRule("mail-service-seckill-route")
                .setCount(5)
                .setIntervalSec(1)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(GatewayParamFlowItem.PARAM_PARSE_STRATEGY_HEADER)
                        .setFieldName("X-User-Id"));
        rules.add(seckillUserRule);

        // 订单接口热点参数限流：每个用户ID QPS=3，防止单用户频繁下单
        GatewayFlowRule orderUserRule = new GatewayFlowRule("mail-service-order-route")
                .setCount(3)
                .setIntervalSec(1)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(GatewayParamFlowItem.PARAM_PARSE_STRATEGY_HEADER)
                        .setFieldName("X-User-Id"));
        rules.add(orderUserRule);

        // 购物车接口热点参数限流：每个用户ID QPS=10
        GatewayFlowRule cartUserRule = new GatewayFlowRule("mail-service-cart-route")
                .setCount(10)
                .setIntervalSec(1)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(GatewayParamFlowItem.PARAM_PARSE_STRATEGY_HEADER)
                        .setFieldName("X-User-Id"));
        rules.add(cartUserRule);

        // 支付接口热点参数限流：每个用户ID QPS=2，防止重复支付请求
        GatewayFlowRule payUserRule = new GatewayFlowRule("mail-service-pay-route")
                .setCount(2)
                .setIntervalSec(1)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(GatewayParamFlowItem.PARAM_PARSE_STRATEGY_HEADER)
                        .setFieldName("X-User-Id"));
        rules.add(payUserRule);

        // 加载限流规则到Sentinel
        com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager.loadRules(rules);
    }

}
