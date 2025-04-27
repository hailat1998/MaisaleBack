package com.hd.misale.Misale.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hd.misale.Misale.dto.ProverbResponse;
import com.hd.misale.Misale.langchain.ChatLanguageModelManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
@Slf4j
public class MyMeaningService {

    private final ChatLanguageModelManager modelManager;
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MyMeaningService(ChatLanguageModelManager modelManager,
                            ReactiveRedisTemplate<String, Object> redisTemplate,
                            ObjectMapper objectMapper) {
        this.modelManager = modelManager;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Mono<ProverbResponse> getMeaning(String proverb) {
        String cacheKey = "proverb:" + proverb.hashCode(); // Better key handling

        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(value -> {
                    try {
                        ProverbResponse response = objectMapper.convertValue(value, ProverbResponse.class);
                        return Mono.just(response);
                    } catch (Exception e) {
                        log.warn("Failed to deserialize cached value", e);
                        return Mono.empty();
                    }
                })
                .switchIfEmpty(fetchAndCacheMeaning(proverb, cacheKey))
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(e -> {
                    log.error("Error fetching meaning", e);
                    return getCachedErrorFallback(cacheKey);
                });
    }

    private Mono<ProverbResponse> fetchAndCacheMeaning(String proverb, String cacheKey) {
        return Mono.zip(
                        getEnglishMeaning(proverb),
                        getAmharicMeaning(proverb)
                )
                .map(tuple -> new ProverbResponse(tuple.getT1(), tuple.getT2()))
                .flatMap(response -> {
                    try {
                        Object value = objectMapper.convertValue(response, Object.class);
                        return redisTemplate.opsForValue()
                                .set(cacheKey, value, Duration.ofDays(3))
                                .thenReturn(response);
                    } catch (Exception e) {
                        log.warn("Failed to serialize for cache", e);
                        return Mono.just(response); // Continue without caching
                    }
                });
    }
    private Mono<ProverbResponse> getCachedErrorFallback(String cacheKey) {
        return redisTemplate.opsForValue().get(cacheKey)
                .cast(ProverbResponse.class)
                .defaultIfEmpty(new ProverbResponse(
                        "Temporary error",
                        "ጊዜያዊ ስህተት"
                ));
    }

    public Mono<String> getEnglishMeaning(String proverb) {
        return Mono.fromCallable(() -> modelManager.meaningInEnglish(proverb))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> getAmharicMeaning(String proverb) {
        return Mono.fromCallable(() -> modelManager.meaningInAmharic(proverb))
                .subscribeOn(Schedulers.boundedElastic());
    }
}