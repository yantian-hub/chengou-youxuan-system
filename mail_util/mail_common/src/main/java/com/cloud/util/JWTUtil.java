package com.cloud.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类 - 用于生成和解析用户身份令牌
 * 密钥通过环境变量JWT_SECRET配置，避免硬编码泄露
 */
public class JWTUtil {

    // 从环境变量读取JWT密钥，未配置时使用默认值（仅开发环境）
    private static final String SECRET = System.getenv("JWT_SECRET") != null
        ? System.getenv("JWT_SECRET")
        : "default-secret-key-for-development-only-change-in-production";

    // 基于HMAC-SHA256算法生成签名密钥
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Token有效期：7天（单位：毫秒）
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @return 加密后的Token字符串
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY)
                .compact();
    }

    /**
     * 从Token中解析用户ID
     * @param token JWT Token字符串
     * @return 用户ID，解析失败抛出异常
     */
    public static Long getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }
}
