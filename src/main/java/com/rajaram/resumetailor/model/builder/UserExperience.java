package com.rajaram.resumetailor.model.builder;

import lombok.Data;

@Data
public class UserExperience {

    private String role;
    private String company;
    private String location;
    private String duration;

    private String description;
    private ExperienceType type;
}