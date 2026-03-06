package com.rajaram.resumetailor.model;

import lombok.Data;
import java.util.List;

@Data
public class Skills {

    private List<String> languages;
    private List<String> backend;
    private List<String> frontend;
    private List<String> databases;
    private List<String> cloud;
    private List<String> tools;
    private List<String> concepts;
}