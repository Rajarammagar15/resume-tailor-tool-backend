package com.rajaram.resumetailor.model.builder;

import lombok.Data;
import java.util.List;

@Data
public class ProjectBullets {

    private String name;
    private List<String> bullets;
}