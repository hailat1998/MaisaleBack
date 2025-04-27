package com.hd.misale.Misale.controller;

import com.hd.misale.Misale.dto.ProverbRequest;
import com.hd.misale.Misale.dto.ProverbResponse;
import com.hd.misale.Misale.service.MyMeaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/meaning", consumes = "application/json", produces = "application/json")
public class MyMeaningController {

    @Autowired
    MyMeaningService myMeaningService;

    @PostMapping("/en-meaning")
    public Mono<String> enMeaning(@RequestBody ProverbRequest request) {
        String proverb = request.getProverb();
        return myMeaningService.getEnglishMeaning(proverb);
    }

    @PostMapping("/am-meaning")
    public Mono<String> amMeaning(@RequestBody ProverbRequest request) {

        String proverb = request.getProverb();

        return myMeaningService.getAmharicMeaning(proverb);
    }

    @PostMapping()
    public Mono<ProverbResponse> getMeaning(@RequestBody ProverbRequest request) {

        String proverb = request.getProverb();

        return myMeaningService.getMeaning(proverb);
    }

}