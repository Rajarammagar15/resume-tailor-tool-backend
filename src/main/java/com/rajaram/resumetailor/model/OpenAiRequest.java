package com.rajaram.resumetailor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiRequest {

    private String model;
    private List<OpenAiMessage> messages;
    private double temperature;
}