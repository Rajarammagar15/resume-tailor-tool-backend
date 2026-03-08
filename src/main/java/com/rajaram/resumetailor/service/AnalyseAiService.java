package com.rajaram.resumetailor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajaram.resumetailor.model.AnalyzeResponse;
import com.rajaram.resumetailor.util.OpenAiClient;
import com.rajaram.resumetailor.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyseAiService {

    private final OpenAiClient openAiClient;
    private final Util util;
    private final ObjectMapper mapper;

    @Value("${openai.model}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            You are an ATS resume optimization expert.
            
            Your task is to analyze a resume against a Job Description and return a structured analysis.
            
            Important rules:
            
            • Do NOT fabricate technologies, achievements, or experience.
            • Preserve all factual information from the resume.
            • Improve wording and organization but do not invent new facts.
            • Maintain the same order of experience and projects as provided.
            
            ------------------------
            Experience Type Detection
            ------------------------
            
            Every experience entry MUST include a "type" field.
            
            Allowed values:
            FULL_TIME
            INTERNSHIP
            
            Determine the type using these rules:
            
            1. If the role title contains:
            Intern, Internship, Trainee, Industrial Training
            → type = "INTERNSHIP"
            
            2. If the resume section heading includes:
            Internship, Internships, Internship Experience
            → type = "INTERNSHIP"
            
            3. If the section heading includes:
            Experience, Work Experience, Professional Experience
            → type = "FULL_TIME"
            
            4. If unclear, default to:
            "type": "FULL_TIME"
            
            Never omit the "type" field.
            
            ------------------------
            Additional Skills Handling
            ------------------------
            
            If additional skills are provided by the user but are not present in the resume:
            
            • Treat them as confirmed skills from the user.
            • They may improve ATS match score.
            • Add them naturally to the skills section if relevant.
            • Never remove valid skills already present in the resume.
            • Only incorporate them into experience or project bullets if logically supported.
            
            Never fabricate achievements just to include a skill.
            
            ------------------------
            Analysis Tasks
            ------------------------
            
            1. Extract required skills from the Job Description.
            2. Extract skills from the Resume.
            3. Compute a match score (0–100).
            4. Provide actionable improvement suggestions.
            5. Return the rewritten structured resume.
            
            ------------------------
            Output Rules
            ------------------------
            
            Return STRICT valid JSON only.
            Do not include explanations or markdown.
            
            JSON schema:
            
            {
              "score": 0,
              "extractedJdSkills": [],
              "extractedResumeSkills": [],
              "suggestions": [],
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
            }""";

    public AnalyzeResponse rewriteResume(String jd,
                                         String resumeText,
                                         List<String> extraSkills) {
        try {
            String userPrompt = buildUserPrompt(jd, resumeText, extraSkills);

            String response = openAiClient.chat(
                    "analyzer",
                    model,
                    SYSTEM_PROMPT,
                    userPrompt,
                    0.3
            );
            AnalyzeResponse analyzeResponse =
                    mapper.readValue(util.cleanJson(response), AnalyzeResponse.class);
            return enrichScore(analyzeResponse);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze resume", e);
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

    private AnalyzeResponse enrichScore(AnalyzeResponse analyzeResponse) {

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
