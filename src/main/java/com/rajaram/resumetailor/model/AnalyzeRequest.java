package com.rajaram.resumetailor.model;

import lombok.Data;

import java.util.List;

@Data
public class AnalyzeRequest {

    private String jobDescription;

    private List<String> extraSkills;

    private List<String> extraProjects;

    private List<String> extraExperience;
}