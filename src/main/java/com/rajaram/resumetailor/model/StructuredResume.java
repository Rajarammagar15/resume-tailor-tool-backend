package com.rajaram.resumetailor.model;

import lombok.Data;

import java.util.List;

@Data
public class StructuredResume {
    private Header header;
    private String summary;
    private Skills skills;
    private List<Experience> experience;
    private List<Education> education;
    private List<String> certifications;
    private List<Project> projects;
}