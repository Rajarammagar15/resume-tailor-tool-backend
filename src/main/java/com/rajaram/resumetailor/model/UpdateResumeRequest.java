package com.rajaram.resumetailor.model;

import lombok.Data;

@Data
public class UpdateResumeRequest {
    private String analysisId;
    private TemplateType template;
    private Skills skills;
}