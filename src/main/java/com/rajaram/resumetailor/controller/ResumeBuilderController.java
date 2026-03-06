package com.rajaram.resumetailor.controller;

import com.rajaram.resumetailor.model.CachedAnalysis;
import com.rajaram.resumetailor.model.ResumeResponse;
import com.rajaram.resumetailor.model.StructuredResume;
import com.rajaram.resumetailor.model.builder.ResumeBuilderRequest;
import com.rajaram.resumetailor.service.AnalysisCacheService;
import com.rajaram.resumetailor.service.ResumeBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeBuilderController {

    private final ResumeBuilderService builderService;
    private final AnalysisCacheService analysisCacheService;

    @PostMapping("/generate")
    public ResumeResponse generate(@RequestBody ResumeBuilderRequest request)
            throws Exception {

        StructuredResume resume = builderService.buildResume(request);

        CachedAnalysis cached = new CachedAnalysis(resume, null);

        String analysisId = analysisCacheService.store(cached);

        ResumeResponse response = new ResumeResponse();
        response.setAnalysisId(analysisId);
        response.setResume(resume);

        return response;
    }
}