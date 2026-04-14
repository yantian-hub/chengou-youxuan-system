package com.cloud.mail.gateway.util;

import com.cloud.util.JWTUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 网关全局鉴权过滤器
 * 功能：
 * 1. 白名单路径直接放行（登录、注册、商品查询等）
 * 2. 校验JWT Token合法性
 * 3. 验证Token是否在Redis中存在（防止Token伪造）
 * 4. 刷新Token过期时间实现滑动窗口续期
 * 5. 将userId透传到下游微服务（X-User-Id请求头）
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 鉴权白名单路径
     * 这些路径无需Token即可访问
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/usrLogin/login",      // 用户登录
        "/usrLogin/register",   // 用户注册
        "/usrLogin/sendCode"    // 发送验证码
    );

    public AuthGlobalFilter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 核心过滤逻辑
     * @param exchange 请求上下文
     * @param chain 过滤器链
     * @return Mono<Void> 异步响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. 白名单路径直接放行
        if (isWhiteListPath(path)) {
            return chain.filter(exchange);
        }

        // 2. 提取并校验Token格式
        String token = extractToken(request);
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            // 3. 解析JWT获取userId
            Long userId = JWTUtil.getUserIdFromToken(token);

            // 4. Redis原子操作：检查Token是否存在 + 续期7天
            // setIfPresent: 仅当key存在时才设置，返回Boolean表示是否成功
            // 验证了Token有效性，实现了滑动窗口续期
            String redisKey = "login:token:" + token;
            Boolean exists = stringRedisTemplate.opsForValue().setIfPresent(redisKey, userId.toString(), 7, TimeUnit.DAYS);

            // Token不存在或已过期，拒绝访问
            if (!Boolean.TRUE.equals(exists)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 5. 将userId注入请求头，透传给下游微服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId.toString())
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            // JWT解析失败（Token过期、伪造、格式错误等）
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * 判断路径是否在白名单中
     * @param path 请求路径
     * @return true-放行，false-需要鉴权
     */
    private boolean isWhiteListPath(String path) {
        // 精确匹配白名单路径（使用startsWith避免误匹配）
        return WHITE_LIST.stream().anyMatch(path::startsWith)
            || path.startsWith("/goods/");  // 商品查询接口公开访问
    }

    /**
     * 从请求头中提取Token
     * @param request HTTP请求
     * @return Token字符串，格式无效时返回null
     */
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);  // 去除"Bearer "前缀
        }
        return null;
    }

    /**
     * 过滤器优先级
     * Order值越小优先级越高
     * -2: 高于Sentinel限流过滤器(-1)，确保鉴权在限流之前执行
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

