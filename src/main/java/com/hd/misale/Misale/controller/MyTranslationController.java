package com.hd.misale.Misale.controller;


import com.hd.misale.Misale.service.MyTranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/translate", consumes = "application/json" , produces= "application/json")
public class MyTranslationController {

    @Autowired
    MyTranslationService myTranslationService;

    @PostMapping("/la2am")
    public Mono<String> latin2amharic(@RequestBody String latinAmharicText) {
        return myTranslationService.getAmharicFidel(latinAmharicText);
    }

    @PostMapping("/en2am")
    public Mono<String> english2amharic(@RequestBody String englishText) {
        return myTranslationService.getAmharicTranslation(englishText);
    }
}
