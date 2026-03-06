package com.rajaram.resumetailor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzeResponse {

    @JsonProperty("score")
    private Integer aiScore;
    private Integer keywordScore;
    private Set<String> matchedSkills;
    private Set<String> missingSkills;
    private List<String> extractedResumeSkills;
    private List<String> extractedJdSkills;
    private List<String> suggestions;
    private StructuredResume structuredResume;
}