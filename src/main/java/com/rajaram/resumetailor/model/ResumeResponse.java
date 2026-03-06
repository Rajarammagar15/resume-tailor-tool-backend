package com.rajaram.resumetailor.model;

import lombok.Data;

import java.util.Set;
import java.util.List;

@Data
public class ResumeResponse {

    private String analysisId;

    private StructuredResume resume;

    private Integer aiScore;
    private Integer keywordScore;

    private Set<String> matchedSkills;
    private Set<String> missingSkills;

    private List<String> suggestions;

}