package com.rajaram.resumetailor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TailorResponse {

    private Integer score;
    private List<String> missingKeywords;
    private String rewrittenResume;
}
