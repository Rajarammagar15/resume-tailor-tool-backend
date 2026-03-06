package com.rajaram.resumetailor.service;

import com.rajaram.resumetailor.model.AnalyzeResponse;
import com.rajaram.resumetailor.util.Util;
import com.rajaram.resumetailor.model.TailorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIRewriteService {

    private final WebClient openAiWebClient;

    private final Util util;

    @Value("${openai.model}")
    private String model;

    public AnalyzeResponse rewriteResume(String jd, String resumeText) {

        String prompt = """
                You are an ATS resume optimization expert.

                Job Description:
                %s

                Resume:
                %s

                Tasks:
                1. Extract required skills.
                2. Calculate match percentage (0-100).
                3. List skills included in JD .
                4. List skills included in resume.
                5. Provide improvement suggestions.
                6. Return the rewritten resume in structured JSON format with the following schema.
                7. Preserve truth.

                Return STRICT JSON:
                {
                    "score": 0,
                    "extractedJdSkills": [
                        "jdSkill1",
                        "jdSkill2"
                    ],
                    "extractedResumeSkills": [
                        "resumeSkill1",
                        "resumeSkill2"
                    ],
                    "suggestions": [
                        "suggestion1",
                        "suggestion2"
                    ],
                    "structuredResume": {
                        "header": {
                            "name": "",
                            "location": "",
                            "email": "",
                            "phone": "",
                            "linkedin":"",
                            "github":""
                        },
                        "summary": "",
                        "skills": {
                            "languages": [],
                            "backend": [],
                            "databases": [],
                            "tools": [],
                            "frontend": [],
                            "cloud": [],
                            "concepts": []
                        },
                        "experience": [
                            {
                                "company": "",
                                "role": "",
                                "location": "",
                                "duration": "",
                                "bullets": []
                            }
                        ],
                        "education": [
                            {
                                "institution": "",
                                "degree": "",
                                "duration": "",
                                "location":"",
                                "grade":""
                            }
                        ],
                        "certifications": []
                    }
                }
                """.formatted(jd, resumeText);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );

        var response = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        try {
            var choices = (List<Map<String, Object>>) response.get("choices");
            var message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            ObjectMapper mapper = new ObjectMapper();
            AnalyzeResponse analyzeResponse = mapper.readValue(util.cleanJson(content), AnalyzeResponse.class);

            return getFullAnalyzeResponse(analyzeResponse);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private AnalyzeResponse getFullAnalyzeResponse(AnalyzeResponse analyzeResponse) {
        Set<String> jdSkills = analyzeResponse.getExtractedJdSkills()
                .stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<String> resumeSkills = analyzeResponse.getExtractedResumeSkills()
                .stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<String> matched = new HashSet<>(jdSkills);
        matched.retainAll(resumeSkills);

        Set<String> missing = new HashSet<>(jdSkills);
        missing.removeAll(resumeSkills);

        int keywordScore = 0;
        if (!jdSkills.isEmpty()) {
            keywordScore = (int) (((double) matched.size() / jdSkills.size()) * 100);
        }
        analyzeResponse.setMatchedSkills(matched);
        analyzeResponse.setMissingSkills(missing);
        analyzeResponse.setKeywordScore(keywordScore);
        return analyzeResponse;
    }
}
