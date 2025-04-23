package com.hd.misale.Misale.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProverbRequest {
    private String proverb;


    public ProverbRequest() {
    }


    public ProverbRequest(String proverb) {
        this.proverb = proverb;
    }

}