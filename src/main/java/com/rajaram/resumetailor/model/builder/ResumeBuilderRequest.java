package com.rajaram.resumetailor.model.builder;

import com.rajaram.resumetailor.model.*;
import lombok.Data;

import java.util.List;

@Data
public class ResumeBuilderRequest {

    private Header header;
    private Skills skills;
    private int yearsOfExperience;
    private List<UserExperience> experience;
    private List<Project> projects;
    private List<Education> education;
    private List<String> certifications;
    private String jobDescription;
}