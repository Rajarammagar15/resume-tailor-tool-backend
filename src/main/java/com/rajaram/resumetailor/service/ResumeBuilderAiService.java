package com.rajaram.resumetailor.service;

import com.rajaram.resumetailor.model.OpenAiMessage;
import com.rajaram.resumetailor.model.OpenAiRequest;
import com.rajaram.resumetailor.model.OpenAiResponse;
import com.rajaram.resumetailor.model.builder.AiResumeResponse;
import com.rajaram.resumetailor.model.builder.ResumeBuilderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeBuilderAiService {

    private final WebClient openAiWebClient;
    private final ObjectMapper mapper;

    private static final String SYSTEM_PROMPT = """
            You are an expert software engineering resume writer.
            
            Your task is to generate resume content ONLY using the information provided by the user.
            
            Do NOT invent technologies, companies, responsibilities, or achievements that are not present in the user input.
            
            If some details are missing, rewrite the existing information professionally but do not fabricate new facts.
            
            ------------------------
            JD Handling Rules
            ------------------------
            
            If Job Description (JD) is EMPTY or NULL:
            Generate a strong generic resume based only on the user's input.
            
            If Job Description (JD) is PROVIDED:
            Optimize wording and bullet points to better match the job description.
            Emphasize skills and technologies that overlap with the JD.
            Do not remove strong technical achievements.
            Rephrase them to match the job description while preserving technical depth.
            
            Do NOT add any technologies, frameworks, or experiences that are not mentioned in the user input.
            
            ------------------------
            Bullet Writing Rules
            ------------------------
            
            Each bullet must follow this structure:
            
            Action Verb + System/Feature Built + Technology Used + Impact.
            Use strong action verbs such as:
            Designed, Implemented, Optimized, Built, Developed, Architected.
            Avoid weak phrases: "worked on", "responsible for", "involved in".
            Include measurable impact whenever possible such as:
            performance improvement, scalability improvement, latency reduction.
            
            If exact metrics are not available, describe impact qualitatively without inventing numbers.
            
            Each experience must contain minimum 7 to 8 bullet points.
            Each project must contain maximum 3 bullet points.
            
            ------------------------
            Summary Rules
            ------------------------
            Generate a concise professional summary of 3–4 lines that highlights:
            
            • Years of experience
            • Core technologies
            • Primary engineering strengths
            
            If a Job Description is provided, slightly tailor the summary toward that role.
            
            ------------------------
            Experience vs Fresher Handling
            ------------------------
            If the user has professional experience:
            Generate bullet points for each experience entry.
            
            If the user experience type is INTERNSHIP, treat it as professional work but slightly shorter and more concise.
            Internships may contain 3–5 bullets instead of 7–8.
            
            If the user has NO experience:
            Return an empty experiences array:
            "experiences": []
            
            Instead, focus on strengthening the projects section.
            
            Projects should highlight:
            • technologies used
            • system design
            • problem solved
            • measurable or qualitative impact
            
            Projects should read like engineering achievements, not academic descriptions.
            
            The "projects" field must ALWAYS be present in the JSON.
            If the user provided projects, return them.
            If no projects exist return:
            "projects": []
            
            ------------------------
            Output Rules
            ------------------------
            Return ONLY valid JSON.
            Do not include explanations, markdown, or extra text.
            
            Use EXACT field names:
            summary
            experiences
            projects
            Maintain the same companies and projects provided in the input.
            
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
            }
            """;

    public AiResumeResponse generateResumeContent(ResumeBuilderRequest request) throws Exception {

        String prompt = buildPrompt(request);

        OpenAiRequest aiRequest = new OpenAiRequest(
                "gpt-4o-mini",
                List.of(new OpenAiMessage("system", SYSTEM_PROMPT),
                        new OpenAiMessage("user", prompt)),
                0.4
        );

        OpenAiResponse response = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(aiRequest)
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .block();

        String content = response.getChoices()
                .get(0)
                .getMessage()
                .getContent();

        return mapper.readValue(content, AiResumeResponse.class);
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