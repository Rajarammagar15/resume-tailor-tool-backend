package com.rajaram.resumetailor.service;

import com.rajaram.resumetailor.model.CachedAnalysis;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AnalysisCacheService {

    private final Map<String, CachedAnalysis> cache = new ConcurrentHashMap<>();

    public String store(CachedAnalysis analysis) {
        String id = UUID.randomUUID().toString();
        cache.put(id, analysis);
        return id;
    }

    public CachedAnalysis get(String id) {
        return cache.get(id);
    }

    public void remove(String id) {
        cache.remove(id);
    }
}