package com.hd.misale.Misale.service;

import com.hd.misale.Misale.langchain.ChatLanguageModelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MyTranslationService {

    private final ChatLanguageModelManager modelManager;

    @Autowired
    public MyTranslationService(ChatLanguageModelManager modelManager) {
        this.modelManager = modelManager;
    }

    @Cacheable(value = "english", key = "#englishText")
    public String getAmharicTranslation(String englishText) {
        return modelManager.translateToAmharic(englishText);
    }

    @Cacheable(value = "latin", key = "#latinAmharicText")
    public String getAmharicFidel(String latinAmharicText) {
        return modelManager.transliterationToAmharic(latinAmharicText);
    }

}