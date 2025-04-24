package com.hd.misale.Misale.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProverbResponse implements Serializable {
    String en_meaning;
    String am_meaning;
}
