package com.rajaram.resumetailor.util;

import org.springframework.stereotype.Component;

@Component
public class Util {

    public String cleanJson(String content) {

        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");

        if (start == -1 || end == -1) {
            throw new RuntimeException("No valid JSON found in AI response");
        }

        return content.substring(start, end + 1);
    }
}
