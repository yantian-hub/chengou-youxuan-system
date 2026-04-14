package com.cloud.mail.service.goods.config;


import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    // 配置 String 结构的序列化
    RedisCacheConfiguration stringConfig = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()));

    // 配置 Hash 结构的序列化
    RedisCacheConfiguration hashConfig = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer())); // Hash value 序列化

    return RedisCacheManager.builder(connectionFactory)
        .withCacheConfiguration("string-cache", stringConfig)
        .withCacheConfiguration("hash-cache", hashConfig)
        .build();
}

}
