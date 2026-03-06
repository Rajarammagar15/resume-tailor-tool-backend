package com.rajaram.resumetailor.controller;

import com.rajaram.resumetailor.mapper.ResumeResponseMapper;
import com.rajaram.resumetailor.model.*;
import com.rajaram.resumetailor.service.AIRewriteService;
import com.rajaram.resumetailor.service.AnalysisCacheService;
import com.rajaram.resumetailor.service.PdfGenerationService;
import com.rajaram.resumetailor.service.PdfParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ResumeController {

    private final PdfParserService pdfParserService;
    private final AIRewriteService aiRewriteService;
    private final PdfGenerationService pdfGenerationService;
    private final AnalysisCacheService cacheService;
    private final ResumeResponseMapper resumeResponseMapper;

    @PostMapping("/analyze")
    public ResponseEntity<ResumeResponse> analyzeResume(@RequestParam("resumeFile") MultipartFile resumeFile, @RequestParam("jobDescription") String jobDescription) {

        String resumeText = pdfParserService.extractText(resumeFile);

        AnalyzeResponse response = aiRewriteService.rewriteResume(jobDescription, resumeText);

        String analysisId = cacheService.store(new CachedAnalysis(response.getStructuredResume(), response));
        return ResponseEntity.ok(resumeResponseMapper.mapResponse(analysisId, response));
    }

    @GetMapping("/pdf/{analysisId}")
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable String analysisId,
            @RequestParam(defaultValue = "CORPORATE") TemplateType template) {

        CachedAnalysis cached = cacheService.get(analysisId);

        if (cached == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = pdfGenerationService.generatePdf(
                cached.getStructuredResume(),
                template
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=tailored_resume.pdf")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
