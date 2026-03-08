package com.rajaram.resumetailor.controller;

import com.rajaram.resumetailor.mapper.ResumeResponseMapper;
import com.rajaram.resumetailor.model.*;
import com.rajaram.resumetailor.service.AnalyseAiService;
import com.rajaram.resumetailor.service.AnalysisCacheService;
import com.rajaram.resumetailor.service.PdfGenerationService;
import com.rajaram.resumetailor.service.PdfParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ResumeAnalyserController {

    private final PdfParserService pdfParserService;
    private final AnalyseAiService analyseAiService;
    private final PdfGenerationService pdfGenerationService;
    private final AnalysisCacheService cacheService;
    private final ResumeResponseMapper resumeResponseMapper;

    @PostMapping("/analyze")
    public ResponseEntity<ResumeResponse> analyzeResume(
            @RequestPart("resumeFile") MultipartFile resumeFile,
            @RequestPart("data") AnalyzeRequest request) {

        String resumeText = pdfParserService.extractText(resumeFile);
        AnalyzeResponse response = analyseAiService.rewriteResume(
                request.getJobDescription(),
                resumeText,
                request.getExtraSkills());

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
