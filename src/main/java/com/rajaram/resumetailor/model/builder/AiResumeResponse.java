package com.rajaram.resumetailor.model.builder;

import lombok.Data;
import java.util.List;

@Data
public class AiResumeResponse {

    private String summary;

    private List<ExperienceBullets> experiences;

    private List<ProjectBullets> projects;
}