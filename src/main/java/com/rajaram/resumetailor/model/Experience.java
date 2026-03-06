package com.rajaram.resumetailor.model;

import lombok.Data;
import java.util.List;

@Data
public class Experience {

    private String company;
    private String role;
    private String location;
    private String duration;

    private List<String> bullets;
}