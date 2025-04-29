package com.hd.misale.Misale.service;

import com.hd.misale.Misale.langchain.ChatLanguageModelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
public class MyTranslationService {

    private final ChatLanguageModelManager modelManager;

    @Autowired
    public MyTranslationService(ChatLanguageModelManager modelManager) {
        this.modelManager = modelManager;
    }

    @Cacheable(value = "english", key = "#englishText")
    public Mono<String> getAmharicTranslation(String englishText) {
        return Mono.fromCallable(() -> modelManager.translateToAmharic(englishText))
                .subscribeOn(Schedulers.boundedElastic()) // Offload blocking call
                .timeout(Duration.ofSeconds(30)) // Add timeout
                .onErrorResume(e -> Mono.just("Translation unavailable")); // Fallback
    }

    @Cacheable(value = "latin", key = "#latinAmharicText")
    public Mono<String> getAmharicFidel(String latinAmharicText) {
        return Mono.fromCallable(() -> modelManager.transliterationToAmharic(latinAmharicText))
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(e -> Mono.just("Transliteration unavailable"));
    }

    @Cacheable(value = "en|la", key="#enOrLa")
    public Mono<String> getAmharic(String enOrLa) {
        return Mono.fromCallable(() -> modelManager.processToAmharic(enOrLa))
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(e -> Mono.just("Transliteration unavailable"));
    }
}