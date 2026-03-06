package com.rajaram.resumetailor.model;

import lombok.Data;
import java.util.List;

@Data
public class Project {

    private String name;
    private String techStack;
    private String description;   // raw user input
    private List<String> bullets; // AI generated
}