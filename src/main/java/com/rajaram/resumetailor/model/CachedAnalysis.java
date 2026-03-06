package com.rajaram.resumetailor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CachedAnalysis {

    private StructuredResume structuredResume;
    private AnalyzeResponse analyzeResponse;
}