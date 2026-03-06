package com.rajaram.resumetailor.mapper;

import com.rajaram.resumetailor.model.AnalyzeResponse;
import com.rajaram.resumetailor.model.ResumeResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ResumeResponseMapper {
    public ResumeResponse mapResponse(String analysisId, AnalyzeResponse analyzeResponse) {
        ResumeResponse response = new ResumeResponse();

        response.setAnalysisId(analysisId);
        response.setResume(analyzeResponse.getStructuredResume());

        response.setAiScore(analyzeResponse.getAiScore());
        response.setKeywordScore(analyzeResponse.getKeywordScore());

        response.setMatchedSkills(analyzeResponse.getMatchedSkills());
        response.setMissingSkills(analyzeResponse.getMissingSkills());

        response.setSuggestions(analyzeResponse.getSuggestions());
        return response;
    }
}
