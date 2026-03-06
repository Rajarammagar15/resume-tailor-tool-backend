package com.rajaram.resumetailor.model.builder;

import lombok.Data;
import java.util.List;

@Data
public class ExperienceBullets {

    private String company;
    private List<String> bullets;
}