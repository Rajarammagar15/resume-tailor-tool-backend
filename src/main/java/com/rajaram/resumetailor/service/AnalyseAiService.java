package com.rajaram.resumetailor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajaram.resumetailor.model.AnalyzeResponse;
import com.rajaram.resumetailor.model.SkillExtractionResponse;
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
//    private static final String SKILL_EXTRACTOR_MODEL = "gpt-4o";

    @Value("${openai.model}")
    private String model;

    private static final String SKILL_EXTRACTION_SYSTEM_PROMPT = """
            You are a resume and job description skill extraction engine.
            
            Extract Skills from:
            1. Job Description
            2. Resume skills section
            3. Entire resume including all summary, skills, experience, projects and certifications sections.
            
            Return STRICT JSON:
            {
             "jdSkills": [],
             "resumeSkillSectionSkills": [],
             "wholeResumeSkills": []
            }
            
            Rules:
            - Normalize skill names (e.g. REST API → REST)
            - Do not include explanations
            - Only technologies, frameworks, tools, databases, programming languages, and architectural concepts
            """;

    private static final String OPTIMIZE_RESUME_SYSTEM_PROMPT = """
            You are an ATS resume optimization engine.
            
            Goal:
            Analyze a resume against a Job Description and return:
            1. ATS match score (0–100)
            2. Missing / weak skills suggestions
            3. An optimized structured resume
            
            Core Rules:
            • Do NOT fabricate technologies, experience, projects, or achievements.
            • Preserve all factual information from the resume.
            • Improve wording, clarity, and ATS relevance only.
            • Maintain the original order of experience and projects.
            
            Skill Integrity Rules:
            You will receive:
            - Extracted JD Skills
            - Skills from the resume skills section
            - All technologies detected in the resume
            - Additional user provided skills (optional)
            
            Allowed technologies in the optimized resume must appear in:
            1. resume skills section
            2. technologies detected anywhere in the resume
            3. additional user provided skills
            
            Technologies appearing only in the Job Description MUST NOT be added.
            
            Skills Optimization Rules:
            • Prioritize technologies appearing in BOTH JD skills and resume technologies.
            • Skills used in experience/projects may be added to the skills section if relevant.
            • Do not include every technology mentioned in the resume.
            • Do not remove valid resume skills unless clearly irrelevant to the Job Description.
            
            Experience Type Detection:
            Each experience entry MUST include a "type" field.
            
            Allowed values:
            FULL_TIME
            INTERNSHIP
            
            Rules:
            If role title contains Intern / Internship / Trainee → INTERNSHIP
            If experience section implies internship → INTERNSHIP
            Otherwise default → FULL_TIME
            
            Resume Writing Rules:
            Rewrite bullets using strong action verbs and clear impact.
            Preferred structure:
            Action + System/Feature + Technology + Impact
            Avoid phrases like:
            worked on, responsible for, involved in.
            Include metrics only if they exist in the resume.
            
            Bullet Limits (Strictly follow):
            Full-time roles → 5–7 bullets
            Internships → 3–4 bullets
            Projects → maximum 2 bullets
            
            Bullet Quality Rules:
            Each bullet should describe:
            • WHAT system or feature was built
            • WHICH technology stack was used
            • WHY it mattered to the system
            
            Avoid vague bullets such as:
            "Developed APIs for backend services"
            
            Prefer:
            "Developed REST APIs using Spring Boot to support wallet transaction processing services"
            
            Technology Distribution Rule:
            Do not repeat the same technology in more than 2 bullets within the same experience. 
            Distribute technologies naturally across bullets such as:
            • programming languages
            • frameworks
            • databases
            • messaging systems
            • cloud services
            • architecture patterns
            
            JD Alignment Rules (CRITICAL):
            Experience bullets MUST emphasize technologies that overlap between:
            • Extracted JD Skills
            • Technologies found in the resume
            
            If a technology exists in BOTH:
            - prioritize mentioning it in experience bullets.
            
            If a bullet describes work related to a JD skill but does not mention the technology explicitly,
            rewrite the bullet to include the technology when it already exists somewhere in the resume.
            
            Example transformation:
            Weak bullet:
            "Developed backend APIs for payment processing"
            Improved bullet:
            "Developed backend REST APIs using Spring Boot and Hibernate for payment processing systems"
            Goal:
            Maximize ATS keyword matching WITHOUT adding technologies not present in the resume.
            
            Technology Mention Rules:
            Whenever possible each experience bullet should include at least one of the following:
            • programming language
            • framework
            • database
            • cloud platform
            • architecture concept
            
            These must come from:
            - resume technologies
            - additional user skills
            Never invent technologies.
            
            Impact Enhancement Rules:
            If the resume experience bullet lacks measurable metrics, improve impact using:
            • scale of system
            • type of system
            • reliability
            • performance
            • automation
            • maintainability
            
            Examples:
            Instead of:
            "Built APIs using Spring Boot"
            Prefer:
            "Built scalable REST APIs using Spring Boot for payment transaction processing"
            
            Do not fabricate numbers or metrics.
            
            Suggestion Rules:
            Suggestions should explain how the candidate can improve ATS match.
            Examples:
            • Missing technologies from JD
            • Weakly represented skills
            • Experience bullets that could emphasize certain technologies
            Do not suggest adding skills the candidate has never used.
            
            Summary Rules:
            Rewrite summary in 4–5 concise lines highlighting:
            • years of experience
            • core technologies
            • engineering strengths
            • systems built
            
            System Context Rule:
            Whenever possible describe the type of system the candidate worked on such as:
            • payment systems
            • transaction platforms
            • microservice architectures
            • distributed systems
            • backend services
            • data processing pipelines
            • enterprise APIs
            
            This improves ATS relevance and recruiter clarity.
            
            Skills Section Categories:
            languages
            backend
            databases
            tools
            frontend
            cloud
            concepts
            
            Do not duplicate skills across categories.
            
            Output Requirements:
            Return STRICT valid JSON only.
            Do not wrap JSON in markdown.
            Do not include text before or after the JSON.
            
            JSON Schema:
            {
              "score": 0,
              "extractedJdSkills": ["skill1","skill2"],
              "extractedResumeSkills": ["skill1","skill2"],
              "suggestions": ["suggestion1", "suggestion2"],
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
                "certifications": ["cert1", "cert2"]
              }
            }
            """;

    public AnalyzeResponse rewriteResume(String jd,
                                         String resumeText,
                                         List<String> extraSkills) {

        try {

            // -------- Stage 1 --------
            SkillExtractionResponse skills =
                    extractSkills(jd, resumeText);

            // -------- Stage 2 --------
            String userPrompt =
                    buildUserPrompt(jd, resumeText, extraSkills, skills);

            String response = openAiClient.chat(
                    "resume-generator",
                    model,
                    OPTIMIZE_RESUME_SYSTEM_PROMPT,
                    userPrompt,
                    0.3
            );

            AnalyzeResponse analyzeResponse =
                    mapper.readValue(util.cleanJson(response), AnalyzeResponse.class);

            return enrichScore(analyzeResponse, skills);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze resume", e);
        }
    }

    private SkillExtractionResponse extractSkills(String jd, String resumeText) throws Exception {
        String prompt = """
                Job Description:
                %s
                
                Resume:
                %s
                """.formatted(jd, resumeText);

        String response = openAiClient.chat(
                "skill-extractor",
                model,
                SKILL_EXTRACTION_SYSTEM_PROMPT,
                prompt,
                0
        );
        return mapper.readValue(util.cleanJson(response), SkillExtractionResponse.class);
    }

    private String buildUserPrompt(String jd,
                                   String resumeText,
                                   List<String> extraSkills,
                                   SkillExtractionResponse skills) {
        String extra = "";
        if (extraSkills != null && !extraSkills.isEmpty()) {
            extra = "Additional Skills Provided By User: " + String.join(", ", extraSkills);
        }

        return """
                Job Description:
                %s
                
                Resume:
                %s
                
                Extracted JD Skills:
                %s
                
                Skills found in resume skills section:
                %s
                
                All technologies detected in resume:
                %s
                
                %s
                
                Resume Optimization Instructions:
                
                1. Identify overlap between Extracted JD Skills and resume technologies.
                2. Ensure those overlapping technologies appear in:
                   - Skills section
                   - Experience bullets where relevant.
                
                3. Rewrite experience bullets so they clearly show:
                   - technology used
                   - system built
                   - engineering contribution
                
                Goal: maximize ATS keyword relevance while preserving factual accuracy.
                
                Generate the optimized structured resume.
                """.formatted(
                jd,
                resumeText,
                safeJoin(skills.getJdSkills()),
                safeJoin(skills.getResumeSkillSectionSkills()),
                safeJoin(skills.getWholeResumeSkills()),
                extra
        );
    }

    private AnalyzeResponse enrichScore(AnalyzeResponse analyzeResponse, SkillExtractionResponse skills) {

        Set<String> jdSkills = skills.getJdSkills()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<String> resumeSkills = skills.getWholeResumeSkills()
                .stream()
                .map(String::toLowerCase)
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

    private String safeJoin(List<String> list) {
        return list == null || list.isEmpty() ? "" : String.join(", ", list);
    }

//    private String truncate(String text, int maxChars) {
//        if (text == null) return "";
//        return text.length() > maxChars ? text.substring(0, maxChars) : text;
//    }
}
