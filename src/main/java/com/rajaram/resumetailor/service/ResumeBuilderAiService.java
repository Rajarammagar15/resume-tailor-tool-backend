package com.rajaram.resumetailor.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajaram.resumetailor.model.builder.AiResumeResponse;
import com.rajaram.resumetailor.model.builder.ResumeBuilderRequest;
import com.rajaram.resumetailor.util.OpenAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeBuilderAiService {

    private final OpenAiClient openAiClient;
    private final ObjectMapper mapper;
    private static final String MODEL = "gpt-4o-mini";

    private static final String SYSTEM_PROMPT = """
            You are an expert software engineering resume writer.
            Your task is to generate professional resume content using ONLY the information provided by the user.
            Never invent technologies, companies, responsibilities, or achievements that are not present in the user input. If information is incomplete, rewrite the provided details professionally but do not fabricate new facts.
            
            ------------------------
            Job Description Handling
            ------------------------
            
            If Job Description (JD) is NULL or empty:
            Generate a strong generic resume using only the user's information.
            
            If Job Description (JD) is provided:
            Optimize wording to better align with the job description.
            Emphasize overlapping skills and technologies.
            Rephrase achievements to better match the JD while preserving technical accuracy.
            
            Do not introduce technologies or experiences not mentioned in the user input.
            
            ------------------------
            Bullet Writing Rules
            ------------------------
            
            Bullets must follow this structure:
            
            Action Verb + Feature/System Built + Technology Used + Impact
            
            Use strong action verbs such as:
            Designed, Implemented, Built, Developed, Optimized, Architected.
            
            Avoid weak phrases like:
            worked on, responsible for, involved in.
            
            Impact can include:
            • performance improvement
            • scalability improvement
            • reliability improvement
            • latency reduction
            • system efficiency
            
            If exact metrics are unavailable, describe impact qualitatively without inventing numbers.
            
            Bullet count rules:
            
            Professional Experience:
            7–8 bullets per role
            
            Internship Experience:
            3–5 bullets per role
            
            Projects:
            Maximum 3 bullets
            
            ------------------------
            Summary Rules
            ------------------------
            
            Generate a concise professional summary (3–4 lines) highlighting:
            
            • years of experience
            • primary technologies
            • engineering strengths
            • type of systems built
            
            If a JD is provided, slightly tailor the summary toward that role.
            
            ------------------------
            Experience vs Fresher Handling
            ------------------------
            
            If the user has professional experience:
            Generate experience bullets normally.
            
            If the user has no professional experience:
            Return an empty experiences array:
            
            "experiences": []
            
            Instead strengthen the projects section to highlight engineering work.
            
            Projects should emphasize:
            • technologies used
            • system design
            • problem solved
            • impact
            
            Projects should read like engineering achievements, not academic descriptions.
            
            Always include the projects field.
            
            If no projects exist return:
            
            "projects": []
            
            ------------------------
            Output Rules
            ------------------------
            
            Return STRICT valid JSON only.
            Do not include explanations or markdown.
            
            Use EXACT field names:
            
            summary
            experiences
            projects
            
            JSON format:
            
            {
              "summary": "...",
              "experiences": [
                {
                  "company": "...",
                  "bullets": []
                }
              ],
              "projects": [
                {
                  "name": "...",
                  "bullets": []
                }
              ]
            }""";

    public AiResumeResponse generateResumeContent(ResumeBuilderRequest request) {
        try {
            String userPrompt = buildPrompt(request);

            String response = openAiClient.chat(
                    "builder",
                    MODEL,
                    SYSTEM_PROMPT,
                    userPrompt,
                    0.4
            );
            return mapper.readValue(response, AiResumeResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate resume content", e);
        }
    }

    private String buildPrompt(ResumeBuilderRequest request) {

        String jd = request.getJobDescription();
        if (jd == null || jd.isBlank()) {
            jd = "NONE";
        }

        return """
                ------------------------
                User Input
                ------------------------
                
                User Skills:
                %s
                
                Years of Experience:
                %f
                
                Experience Description:
                %s
                
                Projects:
                %s
                
                Job Description:
                %s
                
                If Job Description is empty, treat it as NULL.
                """.formatted(
                request.getSkills(),
                request.getYearsOfExperience(),
                request.getExperience(),
                request.getProjects(),
                jd
        );
    }
}