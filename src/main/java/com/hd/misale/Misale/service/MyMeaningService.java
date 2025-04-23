package com.hd.misale.Misale.service;


import com.hd.misale.Misale.langchain.ChatLanguageModelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyMeaningService {

    @Autowired
    ChatLanguageModelManager modelManager;

    public String getEnglishMeaning(String proverb) {

        return modelManager.meaningInEnglish(proverb);
    }

    public String getAmharicMeaning(String proverb) {

        return modelManager.meaningInAmharic(proverb);
    }
}