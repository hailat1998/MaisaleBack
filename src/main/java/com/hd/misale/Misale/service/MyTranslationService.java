package com.hd.misale.Misale.service;

import com.hd.misale.Misale.langchain.ChatLanguageModelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyTranslationService {

    private final ChatLanguageModelManager modelManager;

    @Autowired
    public MyTranslationService(ChatLanguageModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public String getAmharicTranslation(String englishText) {
        return modelManager.translateToAmharic(englishText);
    }

    public String getAmharicFidel(String latinAmharicText) {
        return modelManager.transliterationToAmharic(latinAmharicText);
    }

}