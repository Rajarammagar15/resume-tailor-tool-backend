package com.rajaram.resumetailor.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiClient {
    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

    private final WebClient openAiWebClient;

    public String chat(String category,
                       String model,
                       String systemPrompt,
                       String userPrompt,
                       double temperature) {

        long start = System.currentTimeMillis();

        Map<String, Object> request = Map.of(
                "model", model,
                "input", List.of(
                        Map.of(
                                "role", "system",
                                "content", List.of(
                                        Map.of(
                                                "type", "input_text",
                                                "text", systemPrompt
                                        )
                                )
                        ),
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of(
                                                "type", "input_text",
                                                "text", userPrompt
                                        )
                                )
                        )
                ),
                "temperature", temperature,
                "text", Map.of(
                        "format", Map.of("type", "json_object")
                )
        );

        Map<String, Object> response = openAiWebClient.post()
                .uri("/responses")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException("OpenAI Error: " + errorBody))
                )
                .bodyToMono(Map.class)
                .block();

        logUsage(category, model, response, start);

        List<Map<String, Object>> output =
                (List<Map<String, Object>>) response.get("output");

        Map<String, Object> content = (Map<String, Object>) output.get(0);
        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("content");

        return (String) parts.get(0).get("text");
    }

    private void logUsage(String category,
                          String model,
                          Map<String, Object> response,
                          long startTime) {
        Map<String, Object> usage = (Map<String, Object>) response.get("usage");
        if (usage == null) return;
        long latency = System.currentTimeMillis() - startTime;

        Map<String, Object> details =
                (Map<String, Object>) usage.get("input_tokens_details");
        long promptTokens = ((Number) usage.get("input_tokens")).longValue();
        long cached = details != null && details.get("cached_tokens") != null
                ? ((Number) details.get("cached_tokens")).longValue()
                : 0;
        long billedPromptTokens = promptTokens - cached;

        log.info(
                "LLM_CALL category={} model={} promptTokens={} cachedTokens={} billedPromptTokens={} completionTokens={} totalTokens={} latencyMs={}",
                category,
                model,
                promptTokens,
                cached,
                billedPromptTokens,
                usage.get("output_tokens"),
                usage.get("total_tokens"),
                latency
        );
    }
}