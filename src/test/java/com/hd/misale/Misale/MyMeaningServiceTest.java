package com.hd.misale.Misale;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hd.misale.Misale.dto.ProverbResponse;
import com.hd.misale.Misale.langchain.ChatLanguageModelManager;
import com.hd.misale.Misale.service.MyMeaningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MyMeaningServiceTest {

    @Mock
    private ChatLanguageModelManager modelManager;

    @Mock
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MyMeaningService myMeaningService;

    @BeforeEach
    void setup() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getMeaning_shouldReturnFromCache_whenValueExists() {

        String proverb = "Time is money";
        String cacheKey = "proverb:" + proverb.hashCode();
        ProverbResponse cachedResponse = new ProverbResponse("Cached English", "Cached Amharic");

        when(valueOperations.get(cacheKey)).thenReturn(Mono.just(cachedResponse));
        when(objectMapper.convertValue(any(), eq(ProverbResponse.class))).thenReturn(cachedResponse);


        StepVerifier.create(myMeaningService.getMeaning(proverb))
                .expectNextMatches(response ->
                        response.getEn_meaning().equals("Cached English") &&
                                response.getAm_meaning().equals("Cached Amharic"))
                .verifyComplete();

        verify(valueOperations).get(cacheKey);
        verifyNoMoreInteractions(valueOperations);
    }

    @Test
    void getMeaning_shouldFetchAndCache_whenCacheMiss() {

        String proverb = "Time is money";
        String cacheKey = "proverb:" + proverb.hashCode();
        ProverbResponse newResponse = new ProverbResponse("New English", "New Amharic");

        when(valueOperations.get(cacheKey)).thenReturn(Mono.empty());
        when(modelManager.meaningInEnglish(proverb)).thenReturn("New English");
        when(modelManager.meaningInAmharic(proverb)).thenReturn("New Amharic");
        when(valueOperations.set(eq(cacheKey), any(), eq(Duration.ofDays(3)))).thenReturn(Mono.just(true));
        when(objectMapper.convertValue((Object) any(), (Class<Object>) any())).thenReturn(new Object());


        StepVerifier.create(myMeaningService.getMeaning(proverb))
                .expectNextMatches(response ->
                        response.getEn_meaning().equals("New English") &&
                                response.getAm_meaning().equals("New Amharic"))
                .verifyComplete();

        verify(valueOperations).get(cacheKey);
        verify(valueOperations).set(eq(cacheKey), any(), eq(Duration.ofDays(3)));
        verify(modelManager).meaningInEnglish(proverb);
        verify(modelManager).meaningInAmharic(proverb);
    }


    @Test
    void getEnglishMeaning_shouldCallModelManager() {
        // Arrange
        String proverb = "Time is money";
        when(modelManager.meaningInEnglish(proverb)).thenReturn("English meaning");

        // Act & Assert
        StepVerifier.create(myMeaningService.getEnglishMeaning(proverb))
                .expectNext("English meaning")
                .verifyComplete();

        verify(modelManager).meaningInEnglish(proverb);
    }

    @Test
    void getAmharicMeaning_shouldCallModelManager() {
        // Arrange
        String proverb = "Time is money";
        when(modelManager.meaningInAmharic(proverb)).thenReturn("Amharic meaning");

        // Act & Assert
        StepVerifier.create(myMeaningService.getAmharicMeaning(proverb))
                .expectNext("Amharic meaning")
                .verifyComplete();

        verify(modelManager).meaningInAmharic(proverb);
    }
}