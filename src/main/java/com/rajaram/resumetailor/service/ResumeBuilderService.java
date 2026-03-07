package com.rajaram.resumetailor.service;

import com.rajaram.resumetailor.model.*;
import com.rajaram.resumetailor.model.builder.AiResumeResponse;
import com.rajaram.resumetailor.model.builder.ExperienceBullets;
import com.rajaram.resumetailor.model.builder.ResumeBuilderRequest;
import com.rajaram.resumetailor.model.builder.UserExperience;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeBuilderService {

    private final ResumeAiService aiService;

    public StructuredResume buildResume(ResumeBuilderRequest request) throws Exception {

        AiResumeResponse ai = aiService.generateResumeContent(request);

        StructuredResume resume = new StructuredResume();
        resume.setHeader(request.getHeader());
        resume.setSummary(ai.getSummary());
        resume.setSkills(request.getSkills());
        resume.setExperience(mapExperience(request.getExperience(), ai));
        resume.setProjects(mapProjects(request.getProjects(), ai));
        resume.setEducation(request.getEducation());
        resume.setCertifications(request.getCertifications());
        return resume;
    }

    private List<Experience> mapExperience(List<UserExperience> userExp,
                                           AiResumeResponse ai) {

        List<Experience> list = new ArrayList<>();
        for (int i = 0; i < userExp.size(); i++) {

            UserExperience exp = userExp.get(i);
            ExperienceBullets aiExp = i < ai.getExperiences().size()
                    ? ai.getExperiences().get(i)
                    : null;

            Experience e = new Experience();
            e.setRole(exp.getRole());
            e.setCompany(exp.getCompany());
            e.setLocation(exp.getLocation());
            e.setDuration(exp.getDuration());
            e.setType(exp.getType());

            if (aiExp != null) {
                e.setBullets(aiExp.getBullets());
            }
            list.add(e);
        }
        return list;
    }

    private List<Project> mapProjects(List<Project> projects,
                                      AiResumeResponse ai) {

        System.out.println("User Projects: " + projects);
        System.out.println("AI Projects: " + ai.getProjects());

        if (projects == null || projects.isEmpty()) {
            return List.of();
        }

        if (ai.getProjects() == null || ai.getProjects().isEmpty()) {
            return projects;
        }

        for (int i = 0; i < projects.size(); i++) {

            Project project = projects.get(i);

            if (i < ai.getProjects().size()) {
                project.setBullets(ai.getProjects().get(i).getBullets());
            }
        }

        return projects;
    }
}