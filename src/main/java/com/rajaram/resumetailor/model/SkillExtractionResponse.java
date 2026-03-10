package com.rajaram.resumetailor.model;

import lombok.Data;
import java.util.List;

@Data
public class SkillExtractionResponse {

    private List<String> jdSkills;

    private List<String> resumeSkillSectionSkills;

    private List<String> wholeResumeSkills;
}