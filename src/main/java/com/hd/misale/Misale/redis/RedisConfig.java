package com.hd.misale.Misale.redis;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    // Existing blocking configuration (kept for compatibility)
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setUsername("default");
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        if (StringUtils.hasText(redisPassword)) {
            redisConfig.setPassword(RedisPassword.of(redisPassword));
        }

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    // New reactive template
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        RedisSerializationContext<String, Object> context =
                RedisSerializationContext.<String, Object>newSerializationContext()
                        .key(new StringRedisSerializer())
                        .value(serializer)
                        .hashKey(new StringRedisSerializer())
                        .hashValue(serializer)
                        .build();

        return new ReactiveRedisTemplate<>((ReactiveRedisConnectionFactory) connectionFactory, context);
    }

    // Cache manager (unchanged)
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("proverb",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(7)))
                .withCacheConfiguration("latin",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(24)))
                .withCacheConfiguration("english",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(7)))
                .build();
    }

    // Shared ObjectMapper
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register Java 8 date/time module
        mapper.registerModule(new JavaTimeModule());
        // Disable timestamp serialization
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Other configurations...
        return mapper;
    }
}