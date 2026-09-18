package com.andela.gbv.demo.models;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class UserSession {
    private String userNumber;
    private String language = "en";
    private String currentNodeId = "main";
    private Map<String, Object> context = new HashMap<>();
    private Deque<String> history = new ArrayDeque<>();
    private Instant lastActivity = Instant.now();
    private boolean menuRendered = false;
}
