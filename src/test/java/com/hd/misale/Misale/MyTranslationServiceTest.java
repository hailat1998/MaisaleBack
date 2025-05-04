package com.hd.misale.Misale;

import com.hd.misale.Misale.langchain.ChatLanguageModelManager;
import com.hd.misale.Misale.service.MyTranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class MyTranslationServiceTest {

    @Mock
    private ChatLanguageModelManager modelManager;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private MyTranslationService translationService;

    @BeforeEach
    void setUp() {
        lenient().when(cacheManager.getCache(anyString())).thenReturn(cache);
    }



    @Test
    void allMethods_shouldUseBoundedElasticScheduler() {
        String testText = "test";


        when(modelManager.translateToAmharic(testText)).thenReturn("mockTranslation");
        when(modelManager.transliterationToAmharic(testText)).thenReturn("mockFidel");
        when(modelManager.processToAmharic(testText)).thenReturn("mockProcessed");

        assertScheduler(translationService.getAmharicTranslation(testText), "mockTranslation");

        assertScheduler(translationService.getAmharicFidel(testText), "mockFidel");

        assertScheduler(translationService.getAmharic(testText), "mockProcessed");
    }

    private void assertScheduler(Mono<String> mono, String expectedValue) {
        StepVerifier.create(mono)
                .assertNext(result -> {
                    String threadName = Thread.currentThread().getName();
                    assertTrue(threadName.startsWith("boundedElastic"),
                            "Expected to run on boundedElastic scheduler but was on: " + threadName);
                    assertEquals(expectedValue, result);
                })
                .verifyComplete();
    }
}