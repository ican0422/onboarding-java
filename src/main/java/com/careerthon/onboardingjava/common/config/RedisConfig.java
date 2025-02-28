package com.careerthon.onboardingjava.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {
    // redis 호스트
    @Value("${spring.data.redis.host}")
    private String redisHost;

    // redis 폰트
    @Value("${spring.data.redis.port}")
    private int redisPort;

    // 14일 TTL을 상수로 지정
    private static final long REFRESH_TOKEN_TTL_DAYS = 14;

    // redis 연결
    @Bean
    public RedisConnectionFactory redisConnectionDetails() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    // redis 캐시 매니저 설정
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 기본 캐시 30분
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(30));

        // 리프레쉬 토큰 TTL 지정 해줄 Map
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        // 14일 TTL 설정
        cacheConfigurations.put("refreshToken", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(REFRESH_TOKEN_TTL_DAYS)));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    // RedisTemplate 설정 (리프레시 토큰 저장 및 조회용)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // Key 직렬화
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // Value 직렬화 (JSON)
        return redisTemplate;
    }
}
