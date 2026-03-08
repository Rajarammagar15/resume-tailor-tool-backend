package com.rajaram.resumetailor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajaram.resumetailor.model.AnalyzeResponse;
import com.rajaram.resumetailor.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyseAiService {

    private final WebClient openAiWebClient;

    private final Util util;

    @Value("${openai.model}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            You are an ATS resume optimization expert.
            
            Your job is to analyze a resume against a Job Description and return structured analysis.
            
            Rules:
            - Do NOT hallucinate technologies or experience.
            - Preserve all factual information from the resume.
            - Improve wording but do not fabricate details.
            - Maintain the same order of experience and projects as provided in the resume.
            
            Experience Type Rules:
            Every experience entry MUST include a "type" field.
            Allowed values:
            - "FULL_TIME"
            - "INTERNSHIP"
            Determine the type using the following signals:
            1. If the role title contains words like:
               - Intern
               - Internship
               - Trainee
               - Industrial Training
               then set:
               "type": "INTERNSHIP"
            
            2. If the resume text contains section headings such as:
               - INTERNSHIP
               - Internship Experience
               - Internships
               then treat the entries listed under that section as:
               "type": "INTERNSHIP"
            3. If the section heading contains:
               - Experience
               - Professional Experience
               - Work Experience
               then treat those entries as:
               "type": "FULL_TIME"
            4. If it is unclear from the resume text, default to:
               "type": "FULL_TIME"
            Always include the "type" field for every experience entry.
            Never omit it.
            
            If additional skills are provided by the user that are not present in the resume:
            
            - Treat them as confirmed skills from the user even if they do not appear in the resume text.
            - They may improve ATS match score when compared with the job description.
            - Integrate them naturally into the skills section.
            - Never remove valid skills already present in the resume while optimizing it for the job description.
            - Only incorporate them into experience or project bullet points if logically supported by the resume context.
            - Do not fabricate achievements or experiences just to include the skill.
            
            Tasks:
            1. Extract required skills from the Job Description.
            2. Extract skills from the Resume.
            3. Calculate a match score (0–100).
            4. Provide improvement suggestions.
            5. Return the rewritten resume in structured format.
            
            Return STRICT JSON only.
            Do not include explanations or markdown.
            
            JSON Schema:
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
                         "linkedin": "",
                         "github": ""
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
                           "type": "FULL_TIME or INTERNSHIP",
                           "bullets": []
                         }
                     ],
                     "projects": [
                         {
                             "name": "",
                             "bullets": []
                         }
                     ],
                     "education": [
                         {
                             "institution": "",
                             "degree": "",
                             "duration": "",
                             "location": "",
                             "grade": ""
                         }
                     ],
                     "certifications": []
                 }
             }
            """;

    public AnalyzeResponse rewriteResume(String jd, String resumeText, List<String> extraSkills) {

        String userPrompt = buildUserPrompt(jd, resumeText, extraSkills);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userPrompt)
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

    private String buildUserPrompt(String jd,
                                   String resumeText,
                                   List<String> extraSkills) {

        String skillsSection = "";

        if (extraSkills != null && !extraSkills.isEmpty()) {
            skillsSection = """
                    Additional Skills Provided by User:
                    %s
                    
                    These skills are confirmed by the user but may not appear in the resume.
                    If relevant, integrate them naturally into the resume sections such as:
                    - skills
                    - experience bullets
                    - project bullets
                    """.formatted(String.join(", ", extraSkills));
        }

        return """
                Job Description:
                %s
                
                Resume:
                %s
                
                %s
                
                Update the structured resume accordingly.
                """.formatted(jd, resumeText, skillsSection);
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
